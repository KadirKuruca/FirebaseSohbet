package com.kadirkuruca.firebaseauthentication


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthProvider
import kotlinx.android.synthetic.main.fragment_onaymail.*
import kotlinx.android.synthetic.main.fragment_onaymail.view.*


/**
 * A simple [Fragment] subclass.
 */
class OnayMailFragment : DialogFragment() {

    lateinit var mContext : FragmentActivity

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_onaymail, container, false)

        mContext = activity

        // var butonGonder = view.findViewById<Button>(R.id.btnDialogIptal) Bu şekilde elamanı atadık.

        view.btnDialogGonder.setOnClickListener {
            if(etDialogEmail.text.isNotEmpty() && etDialogSifre.text.isNotEmpty()){

                onayMailiniTekrarGonder(view.etDialogEmail.text.toString(),view.etDialogSifre.text.toString())
                Toast.makeText(context,"Gönderildi.",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context,"Alanları Boş Bırakmayınız.",Toast.LENGTH_SHORT).show()
            }
        }

        view.btnDialogIptal.setOnClickListener {
            dialog.dismiss()
        }

        return view
    }

    private fun onayMailiniTekrarGonder(mail: String, sifre: String) {
        var credential = EmailAuthProvider.getCredential(mail,sifre)
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        mailiTekrarGonder()
                        dialog.dismiss()
                    }else{
                        Toast.makeText(context,"Email veya Şifre Hatalı."+task.exception?.message,Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun mailiTekrarGonder() {
        var kullanici = FirebaseAuth.getInstance().currentUser
        if(kullanici != null){
            kullanici.sendEmailVerification()
                    .addOnCompleteListener(object : OnCompleteListener<Void> {
                        override fun onComplete(p0: Task<Void>) {
                            if(p0.isSuccessful){
                                Toast.makeText(context, "Email Adresinize Onay Maili Gönderildi.",Toast.LENGTH_SHORT).show()
                            }
                            else{
                                Toast.makeText(context,"Mail Gönderilirken Hata Oluştu :\n"+p0.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        }

                    })
        }
    }


}