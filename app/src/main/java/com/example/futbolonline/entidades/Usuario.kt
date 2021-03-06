package com.example.futbolonline.entidades

import android.os.Parcel
import android.os.Parcelable

class Usuario(
    email: String,
    nombre: String,
    genero: String,
    fechaNacimiento: String,
    contrasenia: String,
    calificacion: Int
) : Parcelable {

    var email: String

    var nombre: String

    var genero: String

    var fechaNacimiento: String

    var contrasenia: String

    var calificacion: Int = 0

    constructor() : this("", "", "", "", "", 0)

    init {
        this.email = email!!
        this.nombre = nombre!!
        this.genero = genero!!
        this.fechaNacimiento = fechaNacimiento!!
        this.contrasenia = contrasenia!!
        this.calificacion = calificacion!!
    }

    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readInt()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(email)
        writeString(nombre)
        writeString(genero)
        writeString(fechaNacimiento)
        writeString(contrasenia)
        writeInt(calificacion)
    }

    override fun toString(): String {
        return "Usuario(mail='$email', nombre='$nombre', genero='$genero', fechaNacimiento='$fechaNacimiento' , contrasenia='$contrasenia', calificacion=$calificacion)"
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Usuario> = object : Parcelable.Creator<Usuario> {
            override fun createFromParcel(source: Parcel): Usuario = Usuario(source)
            override fun newArray(size: Int): Array<Usuario?> = arrayOfNulls(size)
        }
    }
}