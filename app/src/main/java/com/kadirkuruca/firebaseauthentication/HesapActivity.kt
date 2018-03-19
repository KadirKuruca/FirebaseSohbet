package com.kadirkuruca.firebaseauthentication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_hesap.*

class HesapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hesap)

        var kullanici = FirebaseAuth.getInstance().currentUser
        if(kullanici != null){
            etMevcutEmail.setText(kullanici.email)
            if(!kullanici.displayName.isNullOrEmpty())
            etKullaniciAdi.setText(kullanici.displayName)
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
