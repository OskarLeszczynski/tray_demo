package com.example.domain

import java.util.UUID

class IdGenerator {
    def generate(): String = UUID.randomUUID().toString
}
