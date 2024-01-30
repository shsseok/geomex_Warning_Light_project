package alarm;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class alarmService {

    private int VOLUME =0;//소리 0:꺼짐 1~5 소리
    private int RED_LED =0;//빨강 0:꺼짐, 1:켜짐, 2:깜빡
    private int GREEN_LED =0;//초록 0:꺼짐, 1:켜짐, 2:깜빡
    private int YELLOW_LED =0;//노랑 0:꺼짐, 1:켜짐, 2:깜빡
    private int BLUE_LED =0;//노랑 0:꺼짐, 1:켜짐, 2:깜빡
    SimpleDateFormat format = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");

    /**
     * 알람을 울린다.
     *
     * @param count sec 단위
     * @throws Exception
     */
    public synchronized void alarm(int time) throws Exception {

        // 시스템 설정값
        HashMap<String,String> sysParam = new HashMap<String,String>();
        sysParam.put("event.bell_led", "{빨강:Y,초록:Y,노랑:Y,파랑:Y}");
        sysParam.put("event.bell_evt", "Y");
        sysParam.put("event.bell_time", "2");

        VOLUME = 0;
        RED_LED = 0;
        GREEN_LED = 0;
        YELLOW_LED = 0;
        BLUE_LED = 0;
        HashMap<String, Object> map = strToMap( sysParam.get("event.bell_led"));
        if ( sysParam.get("event.bell_evt").equals("Y") ) VOLUME = 0;
        if ( map.get("빨강").equals("Y") ) RED_LED = 2;
        if ( map.get("노랑").equals("Y") ) GREEN_LED = 2;
        if ( map.get("초록").equals("Y") ) YELLOW_LED = 2;
        if ( map.get("파랑").equals("Y") ) BLUE_LED = 2;

        //스태틱 처리.
        System.out.println(format.format(System.currentTimeMillis())+"      alarm Start");
        sendSocket(1);
        if(time ==0) {
            Thread.sleep((Integer.parseInt(sysParam.get("event.bell_time")))*1000);
            System.out.println(format.format(System.currentTimeMillis())+"      alarm end(Smart CCTV)");
        }
        else {
            Thread.sleep(time*1000);
            System.out.println(format.format(System.currentTimeMillis())+"      alarm end(earth)");

        }
        sendSocket(0);
    }
    public String getPrintStackTrace(Exception e) {

        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));

        return errors.toString();
    }
    /**
     * 경광등에 소켓을 연결하고 패킷을 보낸다.
     *
     * @param eventCd  0:led 소리 중지, 1:led 소리 시작, 2:led 상태 호출(미구현)
     */
    private void sendSocket(int eventCd){

        // 시스템 설정값
        String ip = "10.1.74.160";
        int port = Integer.parseInt("20000");
       
        Socket socket = null;
        OutputStream os = null;
        DataOutputStream dos = null;

        InputStream is = null;
        DataInputStream dis = null;

        try {
            socket = new Socket(ip, port);
            socket.setSoTimeout(5000);

            os = socket.getOutputStream();
            dos = new DataOutputStream(os);

            is = socket.getInputStream();
            dis = new DataInputStream(is);
            byte[] sendData = new byte[10];
          
            switch(eventCd){
                case 0:
                    sendData[0] = (byte)0x57;
                    sendData[1] = (byte)0x00;
                    sendData[2] = (byte)0x00;
                    sendData[3] = (byte)0x00;
                    sendData[4] = (byte)0x00;
                    sendData[5] = (byte)0x00;
                    sendData[6] = (byte)0x00;
                    sendData[7] = (byte)0x00;
                    sendData[8] = (byte)0x00;
                    sendData[9] = (byte)0x00;
                    break;
                case 1:
                    sendData[0] = (byte)0x57;
                    sendData[1] = (byte)0x00;
                    sendData[2] = (byte)RED_LED;
                    sendData[3] = (byte)GREEN_LED;
                    sendData[4] = (byte)YELLOW_LED;
                    sendData[5] = (byte)BLUE_LED;
                    sendData[6] = (byte)0x00;
                    sendData[7] = (byte)VOLUME;
                    sendData[8] = (byte)0x00;
                    sendData[9] = (byte)0x00;
                    break;
                case 2:
                    sendData[0] = (byte)0x52;
                    sendData[1] = (byte)0x00;
                    sendData[2] = (byte)0x00;
                    sendData[3] = (byte)0x00;
                    sendData[4] = (byte)0x00;
                    sendData[5] = (byte)0x00;
                    sendData[6] = (byte)0x00;
                    sendData[7] = (byte)0x00;
                    sendData[8] = (byte)0x00;
                    sendData[9] = (byte)0x00;
                    break;
                default :
                    sendData[0] = (byte)0x57;
                    sendData[1] = (byte)0x00;
                    sendData[2] = (byte)0x00;
                    sendData[3] = (byte)0x00;
                    sendData[4] = (byte)0x00;
                    sendData[5] = (byte)0x00;
                    sendData[6] = (byte)0x00;
                    sendData[7] = (byte)0x00;
                    sendData[8] = (byte)0x00;
                    sendData[9] = (byte)0x00;
                    break;
            }
            
            dos.write(sendData);
            //System.out.println("데이터 전송");
            dos.flush();
            System.out.println("통신중...");
            //잠시 대기.
            Thread.sleep(1000);
            os.close();
            is.close();
            dos.close();
            dis.close();
            socket.close();

        } catch (Exception e) {
            System.out.println("led socket >>>>>>>>>>>>>>> error\n"+format.format(System.currentTimeMillis())+getPrintStackTrace(e));
            // System.out.println(e);
        } finally {
            try { if( os!=null ) os.close(); } catch (IOException e) {System.out.println("1");}
            try { if( is!=null ) is.close(); } catch (IOException e) {System.out.println("2");}
            try { if( dos!=null ) dos.close(); } catch (IOException e) {System.out.println("3");}
            try { if( dis!=null )  dis.close(); } catch (IOException e) {System.out.println("4");}
            try { if( socket!=null )  socket.close(); } catch (IOException e) {System.out.println("5");}
        }

        return;
    }


    /**
     * 마지막 콤마를 제거한 JSON 형태를 리턴합니다.
     *
     * @param key
     * @param val
     * @param lastStrRemove
     * @return
     */
    public static HashMap<String, Object> strToMap(String str){
    	//빨강:Y,초록:Y,노랑:Y
        String value = str;
        value = value.substring(1, value.length()-1);
        String[] keyValuePairs = value.split(",");
        HashMap<String, Object> map = new HashMap<String, Object>();

        for(String pair : keyValuePairs)
        {
            String[] entry = pair.split(":");
            map.put(entry[0].trim(), entry[1].trim());
        }

        return map;

    }

}
