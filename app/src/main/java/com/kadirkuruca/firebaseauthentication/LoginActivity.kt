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
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var mAuthStateListener : FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initMyAuthStateListener()

        tvKayit.setOnClickListener {
            intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        btn_giris.setOnClickListener {
            if(etLoginMail.text.isNotEmpty() && etLoginSifre.text.isNotEmpty()){
                progressBarGoster()

                FirebaseAuth.getInstance().signInWithEmailAndPassword(etLoginMail.text.toString(),etLoginSifre.text.toString())
                        .addOnCompleteListener(object : OnCompleteListener<AuthResult>{
                            override fun onComplete(p0: Task<AuthResult>) {

                                if(p0.isSuccessful){
                                    progressBarGizle()
                                    Toast.makeText(this@LoginActivity,"Giriş Başarılı : "+FirebaseAuth.getInstance().currentUser?.email,Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    Toast.makeText(this@LoginActivity,"Giriş Yaparken Hata Oluştu.\n"+p0.exception?.message,Toast.LENGTH_SHORT).show()
                                    progressBarGizle()
                                }
                            }

                        })

            }
            else{
                Toast.makeText(this@LoginActivity,"Alanları Boş Bırakmayınız.",Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun progressBarGoster(){
        progressBarLogin.visibility = View.VISIBLE
    }

    private fun progressBarGizle(){
        progressBarLogin.visibility = View.INVISIBLE
    }

    //AuthStateListener a ilk atamasını yapan fonksiyon.
    private fun initMyAuthStateListener(){

        mAuthStateListener = object : FirebaseAuth.AuthStateListener{

            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var kullanici = p0.currentUser

                if(kullanici != null){ // Kullanıcı Sisteme Giriş Yaptıktan sonra değeri null a değildir.

                    if(kullanici.isEmailVerified){
                        var intentMain = Intent(this@LoginActivity,MainActivity::class.java)
                        startActivity(intentMain)
                        finish()
                    }
                    else{
                        Toast.makeText(this@LoginActivity,"Mail Adresinizi Onayladıktan Sonra Giriş Yapınız.",Toast.LENGTH_SHORT).show()
                        FirebaseAuth.getInstance().signOut()
                    }
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener)
    }
}
