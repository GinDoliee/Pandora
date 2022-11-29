package ts.thunder.storm.pandora

class CommonInfo {

    companion object{


        val TRADE_PRICE = "trade_price"
        val TRADE_VOLUME = "acc_trade_price_24h"
        val TRADE_CHANGE = "signed_change_rate"


        val DIVIDE_VALUE = 1000000
        var AddressInfo = mutableListOf<Stake>()
        val FCT2_ADD_LENGTH = 44
        var FCTValue:Float = 0.0F

        init {

        }

    }
}