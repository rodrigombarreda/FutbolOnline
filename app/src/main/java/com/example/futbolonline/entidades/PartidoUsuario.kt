package com.example.futbolonline.entidades

import android.annotation.SuppressLint
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi

class PartidoUsuario(
    id: String,
    emailUsuario: String,
    nombrePartido: String,
    visibleEnHistorial: Boolean
) : Parcelable {

    var id: String = ""

    var emailUsuario: String = ""

    var nombrePartido: String = ""

    var visibleEnHistorial: Boolean = true

    constructor() : this("", "", "",true)

    init {
        this.id = id!!
        this.emailUsuario = emailUsuario!!
        this.nombrePartido = nombrePartido!!
        this.visibleEnHistorial = visibleEnHistorial!!
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readBoolean()!!
    )

    override fun describeContents() = 0

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(emailUsuario)
        writeString(nombrePartido)
        writeBoolean(visibleEnHistorial)
    }

    override fun toString(): String {
        return "PartidoUsuario(nombreEvento='$id' , emailCreador='$emailUsuario' , fechaYHora='$nombrePartido' , visibleEnHistorial=$visibleEnHistorial)"
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<PartidoUsuario> = object : Parcelable.Creator<PartidoUsuario> {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun createFromParcel(source: Parcel): PartidoUsuario = PartidoUsuario(source)
            override fun newArray(size: Int): Array<PartidoUsuario?> = arrayOfNulls(size)
        }
    }
}