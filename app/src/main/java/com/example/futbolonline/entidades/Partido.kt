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
    calificacionMinima: Int,
    emailCreador: String,
    fechaYHora: String,
    latitud: Double,
    longitud: Double,
    ubicacion: String

) : Parcelable {

    var nombreEvento: String
    var cantidadJugadoresTotales: Int = 0
    var cantidadJugadoresFaltantes: Int = 0
    var generoAdmitido: String
    var edadMinima: Int = 0
    var edadMaxima: Int = 0
    var calificacionMinima: Int = 0
    var emailCreador: String
    var fechaYHora: String
    var latitud: Double = 0.0
    var longitud: Double = 0.0
    var ubicacion: String

    constructor() : this("", 0, 0, "", 0, 0, 0, "", "", 0.0, 0.0, "")

    init {
        this.nombreEvento = nombreEvento!!
        this.cantidadJugadoresTotales = cantidadJugadoresTotales!!
        this.cantidadJugadoresFaltantes = cantidadJugadoresFaltantes!!
        this.generoAdmitido = generoAdmitido!!
        this.edadMinima = edadMinima!!
        this.edadMaxima = edadMaxima!!
        this.calificacionMinima = calificacionMinima!!
        this.emailCreador = emailCreador!!
        this.fechaYHora = fechaYHora!!
        this.latitud = latitud!!
        this.longitud=longitud!!
        this.ubicacion=ubicacion!!
    }

    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readInt()!!,
        source.readInt()!!,
        source.readString()!!,
        source.readInt()!!,
        source.readInt()!!,
        source.readInt()!!,
        source.readString()!!,
        source.readString()!!,
        source.readDouble()!!,
        source.readDouble()!!,
        source.readString()!!
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
        writeString(emailCreador)
        writeString(fechaYHora)
        writeDouble(latitud)
        writeDouble(longitud)
        writeString(ubicacion)
    }

    override fun toString(): String {
        return "Partido(nombreEvento='$nombreEvento', cantidadJugadoresTotales=$cantidadJugadoresTotales ," +
                " cantidadJugadoreFaltantes=$cantidadJugadoresFaltantes , generoAdmitido='$generoAdmitido', edadMinima=$edadMinima ," +
                " edadMaxima=$edadMaxima , calificacionMinima=$calificacionMinima , emailCreador='$emailCreador' , fechaYHora='$fechaYHora' ," +
                " latitud=$latitud , longitud=$longitud , ubicacion=$'$ubicacion')"
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Partido> = object : Parcelable.Creator<Partido> {
            override fun createFromParcel(source: Parcel): Partido = Partido(source)
            override fun newArray(size: Int): Array<Partido?> = arrayOfNulls(size)
        }
    }
}