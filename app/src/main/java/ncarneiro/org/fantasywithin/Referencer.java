package ncarneiro.org.fantasywithin;

import android.app.Activity;

/**
 * Created by TiagoDavi on 30/07/2015.
 */
public class Referencer {

    private static Activity act;
    private static String host = "fantasywithin.ddns.net"; // TODO: rodando no lab_vis2, passar pra um servidor remoto no futuro

    public static void setAct(Activity act) {
        Referencer.act = act;
    }
    public static Activity getAct() {
        return act;
    }

    public static String getHost() {
        return host;
    }
}
