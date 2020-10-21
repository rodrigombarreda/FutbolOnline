package com.example.futbolonline.entidades

import android.os.Parcel
import android.os.Parcelable

class PartidoUsuario(
    id: String,
    emailUsuario: String,
    nombrePartido: String
) : Parcelable {

    var id: String = ""

    var emailUsuario: String = ""

    var nombrePartido: String = ""

    constructor() : this("", "", "")

    init {
        this.id = id!!
        this.emailUsuario = emailUsuario!!
        this.nombrePartido = nombrePartido!!
    }

    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(emailUsuario)
        writeString(nombrePartido)
    }

    override fun toString(): String {
        return "PartidoUsuario(nombreEvento='$id' , emailCreador='$emailUsuario' , fechaYHora='$nombrePartido')"
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<PartidoUsuario> = object : Parcelable.Creator<PartidoUsuario> {
            override fun createFromParcel(source: Parcel): PartidoUsuario = PartidoUsuario(source)
            override fun newArray(size: Int): Array<PartidoUsuario?> = arrayOfNulls(size)
        }
    }
}