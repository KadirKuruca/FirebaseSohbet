package com.kadirkuruca.firebaseauthentication


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.browse.MediaBrowser
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.w3c.dom.Text


/**
 * A simple [Fragment] subclass.
 */
class ProfilResmiFragment : DialogFragment() {

    lateinit var tvGaleridenSec : TextView
    lateinit var tvKameradanCek : TextView

    interface onProfilResimListener{
        fun getImagePath(imagePath : Uri?)
        fun getImageBitmap(bitmap: Bitmap)
    }

    lateinit var myProfilResimListener : onProfilResimListener

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view = inflater!!.inflate(R.layout.fragment_profil_resmi, container, false)

        tvGaleridenSec = view.findViewById(R.id.tvGaleriFoto)
        tvKameradanCek = view.findViewById(R.id.tvKamereFoto)

        tvKameradanCek.setOnClickListener {
            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,200)
        }

        tvGaleridenSec.setOnClickListener {
            var intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent,100)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode==Activity.RESULT_OK && data != null){
            var secilenResimYolu = data.data
            Log.e("KADİR","galeridenSeçilenResimYolu")
            myProfilResimListener.getImagePath(secilenResimYolu)
            dialog.dismiss()

        }
        else if(requestCode == 200 && resultCode == Activity.RESULT_OK && data !=null){
            var cekilenResimYolu = data.extras.get("data") as Bitmap
            Log.e("KADİR","kameradanResimYolu")
            myProfilResimListener.getImageBitmap(cekilenResimYolu)
            dialog.dismiss()
        }
    }

    override fun onAttach(context: Context?) {

        myProfilResimListener = activity as onProfilResimListener

        super.onAttach(context)
    }

}
