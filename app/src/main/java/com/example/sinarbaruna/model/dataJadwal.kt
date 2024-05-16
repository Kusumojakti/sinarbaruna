package com.example.sinarbaruna.model

data class dataJadwal(
    var id : Int,
    var id_moulding : String,
    var username :  String = "",
    var tanggal : String,
    var type_moulding : String,
    var durasi : String,
    var mulai_tanggal : String,
    var user_id : String,
    var keterangan : String
)
