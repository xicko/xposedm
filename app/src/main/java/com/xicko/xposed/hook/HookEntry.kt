package com.xicko.xposed.hook

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import com.xicko.xposed.constants.DEFAULT_LAT
import com.xicko.xposed.constants.DEFAULT_LNG
import com.xicko.xposed.constants.HookPrefs

@InjectYukiHookWithXposed
class HookEntry : IYukiHookXposedInit {
	data class Location(
		val latitude: Double,
		val longitude: Double,
	)

	private fun randomizeCoords(
		savedMultiplier: String,
		location: Location,
	): Location {
		val lat = location.latitude
		val lng = location.longitude

		val multiplier = savedMultiplier.toDoubleOrNull() ?: 1.0

		val mode = (0..2).random()

		val rndm = (0..10).random() * (0.00005 * multiplier)

		if (mode == 0) {
			return Location(latitude = lat + rndm, longitude = lng - rndm)
		} else if (mode == 1) {
			return Location(latitude = lat - rndm, longitude = lng + rndm)
		}

		return Location(lat - rndm, lng - rndm)
	}

	override fun onInit() =
		configs {
			isEnableHookSharedPreferences = true
			isEnableDataChannel = true
			debugLog {
				isEnable = true
			}
			isDebug = true
		}

	override fun onHook() =
		encase {
			loadApp {
				Activity::class
					.resolve()
					.firstMethod {
						name = "onCreate"
						parameters(Bundle::class)
					}.hook {
						before {
							AlertDialog
								.Builder(instance())
								.setTitle("Hooked")
								.setMessage("com.xicko.xposed 3")
								.setPositiveButton("OK", null)
								.show()
						}
					}

				// return false on String.contains() method
				String::class.java
					.resolve()
					.firstMethod {
						name = "contains"
						parameters(CharSequence::class.java)
					}.hook {
						before {
							val searchSequence = args(0).cast<CharSequence?>()?.toString() ?: ""

							if (searchSequence == "magisk" || searchSequence == "zygisk") {
								result = false
							}

							result
						}
					}

				// return 0 (false) on development_settings_enabled
				android.provider.Settings.Secure::class.java
					.resolve()
					.firstMethod {
						name = "getInt"
						parameters(android.content.ContentResolver::class.java, String::class.java)
					}.hook {
						before {
							// val arg1 = args(0)
							val arg2 = args(1).cast<String>().toString()

							if (arg2 == "development_settings_enabled") {
								result = 0
							}

							result
						}
					}
				android.provider.Settings.Secure::class.java
					.resolve()
					.firstMethod {
						name = "getInt"
						parameters(android.content.ContentResolver::class.java, String::class.java, Int::class.java)
					}.hook {
						before {
							// val arg1 = args(0)
							val arg2 = args(1).cast<String>().toString()

							if (arg2 == "development_settings_enabled") {
								result = 0
							}

							result
						}
					}
				android.provider.Settings.Global::class.java
					.resolve()
					.firstMethod {
						name = "getInt"
						parameters(android.content.ContentResolver::class.java, String::class.java)
					}.hook {
						before {
							val arg2 = args(1).cast<String>().toString()

							if (arg2 == "development_settings_enabled") {
								result = 0
							}

							result
						}
					}
				android.provider.Settings.Global::class.java
					.resolve()
					.firstMethod {
						name = "getInt"
						parameters(android.content.ContentResolver::class.java, String::class.java, Int::class.java)
					}.hook {
						before {
							val arg2 = args(1).cast<String>().toString()

							if (arg2 == "development_settings_enabled") {
								result = 0
							}

							result
						}
					}

				// return empty file path on FileInputStream constructor
				java.io.FileInputStream::class.java
					.resolve()
					.firstConstructor {
						parameters(String::class.java)
					}.hook {
						before {
							val filePath = args(0).cast<String>().toString()

							if (filePath == "/proc/mounts" || filePath == "/proc/self/maps") {
								args(0).set("/dev/null")
							}

							result
						}
					}
				java.io.FileInputStream::class.java
					.resolve()
					.firstConstructor {
						parameters(java.io.File::class.java)
					}.hook {
						before {
							val filePath = args(0).cast<java.io.File>()
							val absolutePath = filePath?.absolutePath

							if (absolutePath != null) {
								if (absolutePath == "/proc/mounts" || absolutePath == "/proc/self/maps") {
									args(0).set(java.io.File("/dev/null"))
								}
							}

							result
						}
					}

				android.location.Location::class.java
					.resolve()
					.firstMethod {
						name = "getLatitude"
						emptyParameters()
					}.hook {
						before {
							val savedMultiplier = prefs("hook_settings").get(HookPrefs.MULTIPLIER)
							val lat = prefs("hook_settings").get(HookPrefs.LATITUDE)
							val lng = prefs("hook_settings").get(HookPrefs.LONGITUDE)

							val loc =
								if (lat.isNotBlank() && lng.isNotBlank()) {
									Location(lat.toDoubleOrNull() ?: DEFAULT_LAT.toDouble(), lng.toDoubleOrNull() ?: DEFAULT_LNG.toDouble())
								} else {
									Location(DEFAULT_LAT.toDouble(), DEFAULT_LNG.toDouble())
								}

							// Temporarily disabled
							// result = randomizeCoords(savedMultiplier, loc).latitude
						}
					}

				android.location.Location::class.java
					.resolve()
					.firstMethod {
						name = "getLongitude"
						emptyParameters()
					}.hook {
						before {
							val savedMultiplier = prefs("hook_settings").get(HookPrefs.MULTIPLIER)
							val lat = prefs("hook_settings").get(HookPrefs.LATITUDE)
							val lng = prefs("hook_settings").get(HookPrefs.LONGITUDE)

							val loc =
								if (lat.isNotBlank() && lng.isNotBlank()) {
									Location(lat.toDoubleOrNull() ?: DEFAULT_LAT.toDouble(), lng.toDoubleOrNull() ?: DEFAULT_LNG.toDouble())
								} else {
									Location(DEFAULT_LAT.toDouble(), DEFAULT_LNG.toDouble())
								}

							// Temporarily disabled
							// result = randomizeCoords(savedMultiplier, loc).longitude
						}

						// Spoof isMock boolean
						android.location.Location::class.java
							.resolve()
							.firstMethod {
								name = "isFromMockProvider"
								emptyParameters()
							}.hook {
								before {
									result = false
								}
							}
						android.location.Location::class.java
							.resolve()
							.firstMethod {
								name = "isMock"
								emptyParameters()
							}.hook {
								before {
									result = false
								}
							}
					}
			}
		}
}
