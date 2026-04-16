package com.xicko.xposed.constants

import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData

const val DEFAULT_LAT = "47.918808693663955"
const val DEFAULT_LNG = "106.91759772866669"

object HookPrefs {
	val LATITUDE = PrefsData("latitude", DEFAULT_LAT)
	val LONGITUDE = PrefsData("longitude", DEFAULT_LNG)
	val MULTIPLIER = PrefsData("randomizer_multiplier", "1.0")
}
