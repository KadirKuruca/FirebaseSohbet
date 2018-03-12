package com.kadirkuruca.firebaseauthentication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn_kayit.setOnClickListener {

            if(etEmail.text.isNotEmpty() && etSifre.text.isNotEmpty() && etSifre2.text.isNotEmpty()){

                if(!etSifre.text.toString().equals(etSifre2.text.toString())){

                    Toast.makeText(this@RegisterActivity,"Şifreler Aynı Değil!",Toast.LENGTH_SHORT).show()
                }
                else
                {
                    yeniUyeKayit(etEmail.text.toString(),etSifre.text.toString())
                }
            }
            else{
                Toast.makeText(this@RegisterActivity,"Alanları Boş Bırakmayınız.",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun yeniUyeKayit(mail : String, sifre : String) {
        progressBarGoster()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail,sifre)
                .addOnCompleteListener(object : OnCompleteListener<AuthResult>{
                    override fun onComplete(p0: Task<AuthResult>) {

                        if(p0.isSuccessful){
                            Toast.makeText(this@RegisterActivity, "Kayıt İşlemi Başarılı.",Toast.LENGTH_SHORT).show()
                            onayMailGonder()
                            FirebaseAuth.getInstance().signOut() // Kullanıcının kayıt olduktan sonra email onaylamadan giriş yapmasını engelliyoruz.
                            var intentLogin = Intent(this@RegisterActivity,LoginActivity::class.java)
                            startActivity(intentLogin)
                        }
                        else{
                            Toast.makeText(this@RegisterActivity, "Kayıt İşlemi Sürerken Hata Oluştu.\n"+p0.exception?.message,Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        progressBarGizle()
    }

    private fun onayMailGonder() {

        var kullanici = FirebaseAuth.getInstance().currentUser
        if(kullanici != null){
            kullanici.sendEmailVerification()
                    .addOnCompleteListener(object : OnCompleteListener<Void>{
                        override fun onComplete(p0: Task<Void>) {
                            if(p0.isSuccessful){
                                Toast.makeText(this@RegisterActivity, "Email Adresinize Onay Maili Gönderildi.",Toast.LENGTH_SHORT).show()
                            }
                            else{
                                Toast.makeText(this@RegisterActivity,"Onay Maili Gönderilirken Hata Oluştu :\n"+p0.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        }

                    })
        }
    }
    private fun progressBarGoster(){
        progressBar.visibility = View.VISIBLE
    }

    private fun progressBarGizle(){
        progressBar.visibility = View.INVISIBLE
    }
}
