package com.kadirkuruca.firebaseauthentication

/**
 * Created by Kadir on 19.03.2018.
 */

class Kullanici {

    var isim: String? = null
    var telefon: String? = null
    var profil_resim: String? = null
    var seviye: String? = null
    var kullanici_id: String? = null

    constructor(isim: String, telefon: String, profil_resim: String, seviye: String, kullanici_id: String) {
        this.isim = isim
        this.telefon = telefon
        this.profil_resim = profil_resim
        this.seviye = seviye
        this.kullanici_id = kullanici_id
    }

    constructor() {

    }
}
