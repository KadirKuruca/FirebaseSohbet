package com.kadirkuruca.firebaseauthentication

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_hesap.*

class HesapActivity : AppCompatActivity(), ProfilResmiFragment.onProfilResimListener {

    var izinlerVerildi = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hesap)

        var kullanici = FirebaseAuth.getInstance().currentUser
        if(kullanici != null){
            kullaniciVerileriniOku()
        }

        btnSifreSifirla.setOnClickListener {

            FirebaseAuth.getInstance().sendPasswordResetEmail(kullanici?.email.toString())
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            Toast.makeText(this@HesapActivity,"Şifre Sıfırlama Maili Gönderildi.", Toast.LENGTH_SHORT).show()

                        }else{
                            Toast.makeText(this@HesapActivity,"Mail Gönderilirken Hata Oluştu\n"+task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
        }

        btnDegisiklik.setOnClickListener {
            if(etKullaniciAdi.text.isNotEmpty() && etMevcutPhone.text.isNotEmpty()){

                //Buraya Kontrol Eklenecek
                /*



                */
                    var bilgileriGuncelle = UserProfileChangeRequest.Builder()
                            .setDisplayName(etKullaniciAdi.text.toString())
                            .build()
                    kullanici?.updateProfile(bilgileriGuncelle)!!
                            .addOnCompleteListener { task ->
                                if(task.isSuccessful){
                                    //Değişiklikleri veritabanına kaydediyoruz.
                                    FirebaseDatabase.getInstance().reference
                                            .child("kullanici")
                                            .child(FirebaseAuth.getInstance().currentUser?.uid)
                                            .child("isim")
                                            .setValue(etKullaniciAdi.text.toString())
                                    FirebaseDatabase.getInstance().reference
                                            .child("kullanici")
                                            .child(FirebaseAuth.getInstance().currentUser?.uid)
                                            .child("telefon")
                                            .setValue(etMevcutPhone.text.toString())
                                    Toast.makeText(this@HesapActivity,"Değişiklikler Kaydedildi.",Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    Toast.makeText(this@HesapActivity,"Değişiklikler Kaydedilirken Hata Oluştu\n"+task.exception?.message,Toast.LENGTH_SHORT).show()
                                }
                            }
            }
            else{
                Toast.makeText(this@HesapActivity,"Kaydedilecek Bir Değişiklik Yok.",Toast.LENGTH_SHORT).show()
            }
        }
        btnGuncelle.setOnClickListener {
            if(etMevcutPhone.text.isNotEmpty()){

                var credential = EmailAuthProvider.getCredential(kullanici?.email.toString(), etMevcutPhone.text.toString())
                kullanici!!.reauthenticate(credential)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful){

                                btnParolaGuncelle.setOnClickListener {
                                    parolaGuncelle()
                                }
                                btnMailGuncelle.setOnClickListener {
                                    mailGuncelle()
                                }

                            }
                        constraintGuncelle.visibility = View.VISIBLE
                }
            }
            else{
                Toast.makeText(this@HesapActivity,"Güncelleme İşlemleri İçin Parolanızı Giriniz.",Toast.LENGTH_SHORT).show()
            }
        }

        btnIptalGuncelle.setOnClickListener {

            constraintGuncelle.visibility = View.INVISIBLE
        }

        imgProfilResmi.setOnClickListener {

            if(izinlerVerildi){
                var dialog = ProfilResmiFragment()
                dialog.show(supportFragmentManager,"fotosec")
            }
            else{
                izinIste()
            }
        }

    }

    private fun izinIste() {
        var izinler = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA)
        if(ContextCompat.checkSelfPermission(this,izinler[0])== PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,izinler[1])== PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,izinler[2])== PackageManager.PERMISSION_GRANTED){

            izinlerVerildi = true
        }
        else{
            ActivityCompat.requestPermissions(this,izinler,150)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == 150){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED){
                var dialog = ProfilResmiFragment()
                dialog.show(supportFragmentManager,"fotosec")
            }else{
                Toast.makeText(this,"Galeri veya Kameraya Ulaşmak İçin İzinleri Vermelisiniz.",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getImagePath(imagePath: Uri?) {

    }

    override fun getImageBitmap(bitmap: Bitmap) {

    }

    private fun kullaniciVerileriniOku() {

        var referans = FirebaseDatabase.getInstance().reference
        var kullanici = FirebaseAuth.getInstance().currentUser


        //Metod 1
        var sorgu = referans.child("kullanici")
                .orderByKey()
                .equalTo(kullanici?.uid)
        sorgu.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {

                for(singleSnapshot in p0!!.children){
                    var okunanKullanici = singleSnapshot.getValue(Kullanici::class.java)
                    etKullaniciAdi.setText(okunanKullanici?.isim)
                    etMevcutPhone.setText(okunanKullanici?.telefon)
                    etMevcutEmail.setText(kullanici?.email)
                }
            }

        })
        //Metod 2 orderByChild(kullanici_id) ye göre sıralandı.

        //Metod 3
        var sorgu3 = referans.child("kullanici")
                .child(kullanici?.uid)
                .orderByValue()
                //.equalTo(kullanici?.uid)
        sorgu3.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {

                for(singleSnapshot in p0!!.children){
                    //var okunanKullanici = singleSnapshot.getValue(Kullanici::class.java)
                    //etKullaniciAdi.setText(okunanKullanici?.isim)
                    //etMevcutPhone.setText(okunanKullanici?.telefon)
                    //etMevcutEmail.setText(kullanici?.email)

                    Log.e("ORDERBYVALUES"," "+singleSnapshot?.value.toString())
                }
            }

        })


    }

    private fun mailGuncelle() {

        var kullanici = FirebaseAuth.getInstance().currentUser!!
        if(kullanici != null){

            FirebaseAuth.getInstance().fetchProvidersForEmail(etYeniMail.text.toString())
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {

                            if (task.getResult().providers?.size == 1) {
                                Toast.makeText(this@HesapActivity, "Girdiğiniz Email Adresi Kullanımda.", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                kullanici.updateEmail(etYeniMail.text.toString())
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(this@HesapActivity, "Email Adresiniz Değiştirildi. Tekrar Giriş Yapınız.", Toast.LENGTH_SHORT).show()
                                                FirebaseAuth.getInstance().signOut()
                                                onayMailGonder()
                                                loginEkraninaDon()
                                            } else {
                                                Toast.makeText(this@HesapActivity, "Email Adresiniz Değiştirilirken Hata Oluştu\n" + task.exception?.message, Toast.LENGTH_SHORT).show()
                                            }
                                        }
                            }
                        }
                    }
        }
    }

    private fun parolaGuncelle() {

        var kullanici = FirebaseAuth.getInstance().currentUser!!

        if(kullanici != null){
            if(!etYeniParola.text.toString().equals(etTekrarParola.text.toString())){

                Toast.makeText(this@HesapActivity,"Şifreler Uyuşmuyor.",Toast.LENGTH_SHORT).show()
            }
            else if(etYeniParola.text.equals(etMevcutPhone)){
                Toast.makeText(this@HesapActivity,"Yeni Parola Eskisiyle Aynı Olamaz.",Toast.LENGTH_SHORT).show()
            }
            else{
                kullanici.updatePassword(etYeniParola.text.toString())
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                Toast.makeText(this@HesapActivity,"Parolanız Değiştirildi. Tekrar Giriş Yapınız.",Toast.LENGTH_SHORT).show()
                                FirebaseAuth.getInstance().signOut()
                                loginEkraninaDon()
                            }
                            else{
                                Toast.makeText(this@HesapActivity,"Parola Değiştirilirken Hata Oluştu\n"+task.exception?.message,Toast.LENGTH_SHORT).show()
                            }
                        }
            }



        }
    }

    private fun onayMailGonder() {

        var kullanici = FirebaseAuth.getInstance().currentUser
        if(kullanici != null){
            kullanici.sendEmailVerification()
                    .addOnCompleteListener(object : OnCompleteListener<Void> {
                        override fun onComplete(p0: Task<Void>) {
                            if(p0.isSuccessful){
                                Toast.makeText(this@HesapActivity, "Email Adresinize Onay Maili Gönderildi.",Toast.LENGTH_SHORT).show()
                            }
                            else{
                                Toast.makeText(this@HesapActivity,"Onay Maili Gönderilirken Hata Oluştu :\n"+p0.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        }

                    })
        }
    }

    private fun loginEkraninaDon(){
        var loginIntent = Intent(this@HesapActivity,LoginActivity::class.java)
        startActivity(loginIntent)
        finish()
    }
}
