package com.kadirkuruca.firebaseauthentication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
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
            progressBarGoster()
            if(etKullaniciAdi.text.isNotEmpty() && etMevcutEmail.text.isNotEmpty()){

                if(!etKullaniciAdi.text.toString().equals(kullanici?.displayName.toString())){

                    var bilgileriGuncelle = UserProfileChangeRequest.Builder()
                            .setDisplayName(etKullaniciAdi.text.toString())
                            .build()
                    kullanici?.updateProfile(bilgileriGuncelle)!!
                            .addOnCompleteListener { task ->
                                if(task.isSuccessful){
                                    Toast.makeText(this@HesapActivity,"Değişiklikler Kaydedildi.",Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    Toast.makeText(this@HesapActivity,"Değişiklikler Kaydedilirken Hata Oluştu\n"+task.exception?.message,Toast.LENGTH_SHORT).show()
                                }
                                progressBarGizle()
                            }
                }
            }else{
                Toast.makeText(this@HesapActivity,"Alanları Boş Bırakmayınız.",Toast.LENGTH_SHORT).show()
            }
        }



    }

    private fun progressBarGoster(){
        progressBarHesap.visibility = View.VISIBLE
    }

    private fun progressBarGizle(){
        progressBarHesap.visibility = View.INVISIBLE
    }
}
