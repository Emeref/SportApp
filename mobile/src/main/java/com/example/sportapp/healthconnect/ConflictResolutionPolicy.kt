package com.example.sportapp.healthconnect

enum class ConflictResolutionPolicy {
    NEWER_WINS,         // domyślna
    LOCAL_WINS,         // priorytet danych lokalnych
    HC_WINS,            // priorytet danych z HC
    ASK_USER            // pokaż dialog wyboru
}
