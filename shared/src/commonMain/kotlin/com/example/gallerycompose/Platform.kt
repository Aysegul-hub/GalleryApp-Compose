package com.example.gallerycompose

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform