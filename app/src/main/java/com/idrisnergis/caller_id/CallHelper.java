package com.idrisnergis.caller_id;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;


public class CallHelper {


	/**
	 * Gelen Çağrılar kısmı
	 */
	public class CallStateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:


				Toast.makeText(ctx,
						"Gelen Arama: "+ incomingNumber,
						Toast.LENGTH_LONG).show();
					MainActivity telefon=new MainActivity();
					telefon.telefongonder(incomingNumber);
			}
		}


	}


	public class OutgoingReceiver extends BroadcastReceiver {
	    public OutgoingReceiver() {
	    }

	    @Override
	    public void onReceive(Context context, Intent intent) {
	        String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);


	        Toast.makeText(ctx,
	        		"Dış Arama: "+number,
	        		Toast.LENGTH_LONG).show();

	    }

	}

	private Context ctx;
	private TelephonyManager tm;
	private CallStateListener callStateListener;

	private OutgoingReceiver outgoingReceiver;

	public CallHelper(Context ctx) {
		this.ctx = ctx;

		callStateListener = new CallStateListener();
		outgoingReceiver = new OutgoingReceiver();
	}

	/**
	 * Çağrı başlangıç kontrolü
	 */
	public void start() {
		tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);

		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		ctx.registerReceiver(outgoingReceiver, intentFilter);
	}

	/**
	 * Dur Kontrol et
	 */
	public void stop() {
		tm.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
		ctx.unregisterReceiver(outgoingReceiver);
	}

}
