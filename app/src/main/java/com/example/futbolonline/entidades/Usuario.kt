package com.example.futbolonline.entidades

import android.os.Parcel
import android.os.Parcelable

class Usuario(email: String, nombre: String, genero: String, edad: Int, contrasenia: String): Parcelable{

        var email: String

        var nombre: String

        var genero: String

        var edad: Int = 0

        var contrasenia: String

        constructor() : this("","","",0,"")

        init {
            this.email = email!!
            this.nombre = nombre!!
            this.genero = genero!!
            this.edad = edad!!
            this.contrasenia = contrasenia!!
        }

        constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.readInt(),
        source.readString()!!
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeString(email)
            writeString(nombre)
            writeString(genero)
            writeInt(edad)
            writeString(contrasenia)
        }

        override fun toString(): String {
            return "Usuario(mail='$email', nombre='$nombre', genero='$genero', edad=$edad, contrasenia='$contrasenia')"
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<Usuario> = object : Parcelable.Creator<Usuario> {
                override fun createFromParcel(source: Parcel): Usuario = Usuario(source)
                override fun newArray(size: Int): Array<Usuario?> = arrayOfNulls(size)
            }
        }
}