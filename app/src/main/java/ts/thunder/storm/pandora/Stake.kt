package ts.thunder.storm.pandora

class Stake(address:String,name:String) {

    var FCT_Name:String
    var FCT_Address:String

    var coinAvailableAmount:Float
    var coinDelegatedAmount:Float
    var coinRewardAmount:Float

    init {
        FCT_Address = address
        FCT_Name = name
        coinAvailableAmount = 0.0F
        coinDelegatedAmount = 0.0F
        coinRewardAmount = 0.0F
    }
}