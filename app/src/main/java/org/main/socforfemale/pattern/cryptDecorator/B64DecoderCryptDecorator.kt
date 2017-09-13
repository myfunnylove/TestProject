package org.main.socforfemale.pattern.cryptDecorator

import android.util.Base64

/**
 * Created by Sarvar on 12.09.2017.
 */
class B64DecoderCryptDecorator(crypt: Crypt) : CryptDecorator() {

    var crypto: Crypt = crypt

    override fun getPrm(): String =
            String(Base64.decode(crypto.getPrm(), 0))

}