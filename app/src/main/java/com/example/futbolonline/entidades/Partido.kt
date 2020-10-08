package com.example.futbolonline.entidades

import android.os.Parcel
import android.os.Parcelable

class Partido(
    nombreEvento: String,
    cantidadJugadoresTotales: Int,
    cantidadJugadoresFaltantes: Int,
    generoAdmitido: String,
    edadMinima: Int,
    edadMaxima: Int,
    calificacionMinima: Int
) : Parcelable {

    var nombreEvento: String

    var cantidadJugadoresTotales: Int = 0

    var cantidadJugadoresFaltantes: Int = 0

    var generoAdmitido: String

    var edadMinima: Int = 0

    var edadMaxima: Int = 0

    var calificacionMinima: Int = 0

    constructor() : this("", 0, 0, "", 0, 0, 0)

    init {
        this.nombreEvento = nombreEvento!!
        this.cantidadJugadoresTotales = cantidadJugadoresTotales!!
        this.cantidadJugadoresFaltantes = cantidadJugadoresFaltantes!!
        this.generoAdmitido = generoAdmitido!!
        this.edadMinima = edadMinima!!
        this.edadMaxima = edadMaxima!!
        this.calificacionMinima = calificacionMinima!!
    }

    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readInt()!!,
        source.readInt()!!,
        source.readString()!!,
        source.readInt()!!,
        source.readInt()!!,
        source.readInt()!!
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(nombreEvento)
        writeInt(cantidadJugadoresTotales)
        writeInt(cantidadJugadoresFaltantes)
        writeString(generoAdmitido)
        writeInt(edadMinima)
        writeInt(edadMaxima)
        writeInt(calificacionMinima)
    }

    override fun toString(): String {
        return "Usuario(nombreEvento='$nombreEvento', cantidadJugadoresTotales=$cantidadJugadoresTotales ," +
                " cantidadJugadoreFaltantes=$cantidadJugadoresFaltantes , generoAdmitido='$generoAdmitido', edadMinima=$edadMinima ," +
                " edadMaxima=$edadMaxima , calificacionMinima=$calificacionMinima)"
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Partido> = object : Parcelable.Creator<Partido> {
            override fun createFromParcel(source: Parcel): Partido = Partido(source)
            override fun newArray(size: Int): Array<Partido?> = arrayOfNulls(size)
        }
    }
}