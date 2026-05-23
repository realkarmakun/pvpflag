package io.github.realkarmakun.pvpflag

import net.minecraft.resources.ResourceLocation

fun String.location() = ResourceLocation(Constants.MOD_ID, this)
fun String.vanillaLocation() = ResourceLocation(this)
