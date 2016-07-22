package eliasdowling.com.buoy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.eliasdowling.Buoy";
    AutoCompleteTextView textView;
    private ListView lister;
    HashMap map;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //navigation drawer stuff
        mDrawerList = (ListView)findViewById(R.id.navList);
        addDrawerItems();
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    SharedPreferences prefs = getSharedPreferences("Favorites",MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.clear().commit();
                }else if(position==1){
                    ndbc(view);
                }
            }
        });



        //for testing purposes only we will use this in the sidebar
       /* SharedPreferences prefs = getSharedPreferences("Favorites",MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear().commit();*/

        //Adapter to hold dropdown list
        FilterWithSpaceAdapter<String> adapter = new FilterWithSpaceAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, FULLARRAY);
        //main typable textview
        textView = (AutoCompleteTextView) findViewById(R.id.autoText);
        textView.setAdapter(adapter);


        //allows user to click on dropdown to take them directly to buoy page
        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sendBuoy(view);
            }
        });

        //sets favorites
        map = makeHash(FULLARRAY);
        getFav();
        favView(map);
    }



    @Override
    public void onResume(){
        super.onResume();
        favView(map);
        textView.setText("");
    }

    public HashMap makeHash(String[] buoy){
        HashMap<String,String> map = new HashMap<>();
        for(int i=0;i<buoy.length;i++){
            map.put(buoy[i].substring(0,5),buoy[i]);
        }
        return map;
    }

    /** Called when the user clicks the Enter button */
    public void sendBuoy(View view) {
        // Do something in response to button
        final Intent myIntent = new Intent(this,DataActivity.class);

        if(!textView.getText().toString().equals("")&&map.containsKey(textView.getText().toString().substring(0,5))){
            myIntent.putExtra(EXTRA_MESSAGE, textView.getText().toString());
            startActivity(myIntent);
            onResume();
        }else{
            Snackbar.make(view, textView.getText().toString()+"Invalid buoy. Contact me if you want this buoy added!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public void setclick(TextView t,String fave){
        final String favPlac = fave;
        t.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //frev.removeAllViews();
                Intent newIntent = new Intent(v.getContext(),DataActivity.class);
                newIntent.putExtra(EXTRA_MESSAGE, favPlac);
                startActivity(newIntent);
            }
        });
    }

    public void favView(HashMap map){
        //holds favorites in array
        String[] fave = getFav();
        RelativeLayout favs = (RelativeLayout)findViewById(R.id.expandableLayout1);
        //favs.removeAllViews();
        ExpandableRelativeLayout expandableLayout
                = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout1);

        //final FilterWithSpaceAdapter adapter = new FilterWithSpaceAdapter(this, R.layout.list_item,fave);


        FilterWithSpaceAdapter<String> itemsAdapter =
                new FilterWithSpaceAdapter<String>(this, R.layout.listthing,fave);

        lister = (ListView)findViewById(R.id.listy);

        lister.setAdapter(itemsAdapter);

        lister.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                String  itemValue    = (String) lister.getItemAtPosition(position);

                Intent newIntent = new Intent(view.getContext(),DataActivity.class);
                newIntent.putExtra(EXTRA_MESSAGE, itemValue);
                startActivity(newIntent);
            }

        });
    }

    public String[] getFav(){
        //to expand favorites into longer list:
        //make for loop that iterates through prefs and adds all to array
        SharedPreferences prefs = getSharedPreferences("Favorites",MODE_PRIVATE);
        Map<String,?> keys = prefs.getAll();
        String[] favs = new String[keys.size()];
        int count=0;
        for(Map.Entry<String,?> entry : keys.entrySet()){
            String key = entry.getKey();
            Object val = entry.getValue();
            favs[count] = (String)map.get(val);
            count++;
            //Log.d("",key+val);
        }
        if(favs.length==0) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear().commit();
        }
        return favs;
    }

    public void expandableButton1(View view) {
        ExpandableRelativeLayout expandableLayout
                = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout1);
        //expandableLayout.initLayout(true);
        expandableLayout.toggle(); // toggle expand and collapse
    }

    /**
     * Link to ndbc site
     * @param v
     */
    public void ndbc(View v){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ndbc.noaa.gov/"));
        startActivity(browserIntent);
    }

    private void addDrawerItems() {
        String[] osArray = { "Clear all favorites", "All data courtesy of the NDBC"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
    }
    //array with just buoy code
    private static final String[] BUOYARRAY = new String[]{
        "32ST0","41NT0","41S43","41S46","42OTP","42S39","42S58","42S60","42T58","51WH0","13002","13008","13009","13010","15002","15006","15007","21346","21347","21348","21401","21402","21413","21414","21415","21416","21417","21418","21419","21597","21598","22101","22102","22103","22104","22105","22106","22107","22108","23219","23220","23223","23226","23227","23228","23401","31001","31002","31003","31004","31005","31006","31051","31053","32012","32066","32067","32401","32402","32403","32411","32412","32413","32489","34420","41001","41002","41004","41008","41009","41010","41013","41024","41025","41026","41029","41033","41037","41038","41040","41041","41043","41044","41046","41047","41048","41049","41051","41052","41053","41056","41060","41064","41098","41108","41110","41112","41113","41114","41115","41139","41159","41420","41421","41424","42001","42002","42003","42012","42013","42019","42020","42022","42023","42035","42036","42039","42040","42044","42045","42046","42047","42048","42049","42050","42051","42055","42056","42057","42058","42059","42060","42067","42085","42087","42088","42098","42099","42360","42361","42362","42363","42365","42368","42369","42370","42372","42373","42374","42375","42377","42379","42382","42383","42385","42388","42390","42392","42394","42395","42397","42399","42407","42408","42409","42851","42854","42857","42860","42864","42865","42874","42878","42880","42883","42884","42887","42891","42893","42895","42896","42898","42901","42902","42907","42912","42916","42917","42919","42924","42927","42928","42929","42931","42932","42934","42935","42936","42937","42939","42940","43412","43413","44005","44007","44008","44009","44011","44013","44014","44017","44018","44020","44024","44025","44027","44029","44030","44032","44033","44034","44037","44039","44041","44042","44043","44056","44057","44058","44059","44060","44061","44062","44063","44064","44065","44066","44069","44090","44091","44093","44095","44096","44097","44098","44099","44100","44137","44139","44141","44150","44251","44255","44258","44401","44402","45001","45002","45003","45004","45005","45006","45007","45008","45012","45013","45014","45022","45023","45024","45025","45026","45027","45028","45029","45132","45135","45136","45137","45138","45139","45140","45142","45143","45145","45147","45148","45149","45152","45154","45159","45161","45162","45163","45164","45165","45167","45168","45169","45170","45171","45174","45175","45176","46001","46002","46004","46005","46011","46012","46013","46014","46015","46022","46025","46026","46027","46028","46029","46035","46036","46041","46042","46047","46050","46053","46054","46059","46061","46066","46069","46070","46071","46072","46076","46080","46081","46083","46084","46085","46086","46087","46088","46089","46092","46096","46108","46114","46118","46119","46120","46121","46122","46123","46124","46125","46131","46132","46134","46145","46146","46147","46181","46183","46184","46185","46204","46205","46206","46207","46208","46211","46213","46214","46215","46216","46217","46218","46219","46221","46222","46224","46225","46229","46232","46236","46237","46239","46240","46242","46243","46244","46246","46248","46251","46253","46254","46255","46256","46257","46258","46259","46401","46402","46403","46404","46405","46406","46407","46408","46409","46410","46411","46412","46413","46419","46451","46452","46482","46499","51000","51001","51002","51003","51004","51101","51201","51202","51203","51204","51205","51206","51207","51209","51406","51407","51425","51426","52200","52201","52211","52212","52401","52402","52403","52404","52405","52406","52839","52840","52841","52843","52862","53046","53401","54401","55012","55013","55015","55016","55023","55042","55401","56001","56003","61001","62001","62027","62029","62030","62050","62081","62095","62102","62103","62104","62105","62107","62111","62112","62113","62114","62115","62116","62117","62118","62119","62120","62121","62122","62123","62124","62127","62128","62129","62130","62131","62132","62133","62134","62135","62136","62137","62138","62139","62140","62143","62144","62145","62146","62148","62149","62150","62151","62152","62153","62154","62155","62157","62160","62161","62162","62163","62164","62165","62166","62167","62168","62170","62296","62297","62302","62304","62305","63055","63056","63057","63058","63059","63101","63102","63103","63104","63105","63106","63107","63108","63109","63110","63111","63112","63113","63115","63117","63118","63120","64041","64045","64046","AAMC1","ACFS1","ACQS1","ACXS1","ACYN4","ADKA2","AGCM4","AGMW3","ALIA2","ALXN6","AMAA2","AMRL1","ANMN6","ANRN6","ANTA2","ANVC1","APAM2","APCF1","APNM4","APQF1","APRP7","APXF1","AROP4","ARPF1","ASTO3","ATGM1","ATKA2","AUGA2","AWRT2","BABT2","BARA9","BATN6","BDRN4","BDSP1","BDVF1","BDXC1","BEPB6","BFTN7","BGCF1","BGNN4","BGXN3","BHBM3","BHRI3","BIGM4","BISM2","BKBF1","BKTL1","BKYF1","BLIA2","BLIF1","BLTM2","BLTM3","BNKF1","BOBF1","BRHC3","BRIM2","BRND1","BSBM4","BSCA1","BSKF1","BSLM2","BUFN6","BURL1","BUZM3","BVQW1","BWSF1","BYGL1","BZBM3","CAMM2","CANF1","CAPL1","CARL1","CASM1","CBBV2","CBLO1","CBRW3","CDEA2","CDRF1","CECC1","CFWM1","CHAO3","CHAV3","CHCM2","CHII2","CHLV2","CHQO3","CHSV3","CHTS1","CHYV2","CHYW1","CLBP4","CLKN7","CLSM4","CMAN4","CMTI2","CNBF1","CNDO1","CNII2","COVM2","CPMW1","CPNT2","CPTR1","CPVM2","CPXC1","CQUC1","CRTA1","CRVA2","CSPA2","CWAF1","CWBF1","CWCI.","CWQO3","DARTH","DARTI","DARTL","DARTM","DARTN","DARTO","DARTP","DARTQ","DBLN6","DBQS1","DELD1","DESW1","DISW3","DKCM6","DKKF1","DMSF1","DOMV2","DPHA1","DPIA1","DPXC1","DRFA2","DRSD1","DTLM4","DUKN7","DULM5","EBSW1","EINL1","ELFA2","ELQC1","ELXC1","EPTT2","EREP1","EROA2","ESPP4","EVMC1","FAIO1","FBIS1","FCGT2","FFFC1","FFIA2","FHPF1","FILA2","FMOA1","FMRF1","FOXR1","FPKG1","FPTM4","FPXC1","FRDF1","FRDP4","FRDW1","FREL1","FRFN7","FRVM3","FRWL1","FRXM3","FSKM2","FSNM2","FSTI2","FTGM4","FTPC1","FWYF1","GBIF1","GBQN3","GBTF1","GCVF1","GDMM5","GDQM6","GDWV2","GDXM6","GELO1","GGGC1","GISL1","GKYF1","GNJT2","GRBL1","GRIM4","GSLM4","GTLM4","GTOT2","GTQF1","GTRM4","GTXF1","HBXC1","HBYC1","HCEF1","HCGN7","HIST2","HLNM4","HMRA2","HMSA2","HRBM4","HREF1","HRVC1","ICAC1","ICYA2","IIWC1","ILOH1","IMGP4","IOSN3","ITKA2","JAKI2","JCQN4","JCRN4","JCTN4","JKYF1","JMPN7","JNEA2","JOBP4","JOQP4","JOXP4","JXUF1","KATA1","KATP.","KBBF.","KBQX.","KCHA2","KCVW.","KDAA2","KDLP.","KECA2","KEHC.","KEIR.","KEMK.","KGBK.","KGCA2","KGHB.","KGNA.","KGRY.","KGUL.","KHHV.","KHQI.","KIKT.","KIPN.","KLIH1","KMDJ.","KMIS.","KMZG.","KNSW3","KP53.","KP58.","KP59.","KPTN6","KPTV2","KSCF.","KSPR.","KSQE.","KTNF1","KVAF.","KVBS.","KVKY.","KVOA.","KVQT.","KWHH1","KWJP8","KWNW3","KXIH.","KXPY.","KYWF1","LAMV3","LAPW1","LBRF1","LBSF1","LCLL1","LCNA2","LDLC3","LDTM4","LJAC1","LJPC1","LKWF1","LMBV4","LMDF1","LMFS1","LMRF1","LNDC1","LONF1","LOPL1","LOPW1","LPNM4","LRIF1","LRKF1","LSNF1","LTBV3","LTJF1","LTQM2","LTRM4","LUIT2","LWSD1","LWTV2","LYBT2","MACM4","MAQT2","MAXT2","MBLA1","MBRM4","MBXC1","MCGA1","MCGM4","MCYF1","MCYI3","MDRM1","MEEM4","MGIP4","MGPT2","MGZP4","MHPA1","MHRN6","MISM1","MISP4","MIST2","MKGM4","MLRF1","MLSC1","MLWW3","MNMM4","MNPV2","MOKH1","MQTT2","MRHO1","MRKA2","MRNA2","MROS1","MRSL1","MRYA2","MTBF1","MTKN6","MTYC1","MUKF1","MYPF1","MZXC1","NABM4","NAQR1","NAXR1","NBLP1","NCDV2","NCHT2","NEAW1","NFDF1","NIAN6","NIQS1","NIWS1","NKTA2","NLNC3","NMTA2","NOXN7","NPDW3","NPSF1","NRRF1","NSTP6","NTBC1","NTKM3","NUET2","NWCL1","NWHC3","NWPO3","NWPR1","NWWH1","OBGN6","OBLA1","OBXC1","OCIM2","OHBC1","OKSI2","OKXC1","OLCN6","OLSA2","OOUH1","OPTF1","ORIN7","OSGN6","OSTF1","OVIA2","OWQO1","OWXO1","PACF1","PACT2","PBFW1","PBLW1","PBPA2","PCBF1","PCGT2","PCLF1","PCLM4","PCNT2","PCOC1","PFXC1","PGBP7","PHBP1","PILA2","PILL1","PILM4","PKBW3","PKYF1","PLSF1","PLXA2","PMAF1","PMNT2","PMOA2","PNGW3","PNLM4","PNLM6","PORO3","PORT2","POTA2","PPTA1","PPTM2","PPXC1","PRDA2","PRJC1","PRTA2","PRUR1","PRYC1","PSBC1","PSBM1","PSCM4","PSLC1","PSTL1","PSTN6","PTAT2","PTAW1","PTBM6","PTCR1","PTIM4","PTIT2","PTLA2","PTOA1","PTRP4","PTWW1","PVGF1","PWAW3","PXOC1","PXSC1","QPTR1","RARM6","RCKM4","RCMC1","RCPT2","RCRN6","RCYF1","RDDA2","RDYD1","RF001","RKQF1","RKXF1","RLIT2","RLOT2","ROAM4","ROBN4","RPLV2","RPRN6","RSJT2","RTAT2","RTYC1","RYEC1","SACV4","SANF1","SAPF1","SAQG1","SAUF1","SAXG1","SBEO3","SBIO1","SBLM4","SBPT2","SCLD1","SCQC1","SCQN6","SDBC1","SDHN4","SDRT2","SEFO3","SEQA2","SETO3","SFXC1","SGNW3","SGOF1","SHBL1","SHPF1","SIPF1","SISA2","SISW1","SJNP4","SJOM4","SJSN4","SKTA2","SLIM2","SLOO3","SLVM5","SNDA2","SNDP5","SPGF1","SPLL1","SREF1","SRFW1","SRLM4","SRST2","SSBN7","STDM4","SVNM4","SWLA2","SWPM4","SWPV2","SXHW3","SYWW3","TAQT2","TBIM4","TBYF1","TCBM2","TCMW1","TCNW1","TCVF1","TDPC1","TESL1","TFBLK","THLO1","THRO1","TIBC1","TIQC1","TIXC1","TLBO3","TOKW1","TPEF1","TPLM2","TRDF1","TRRF1","TSHF1","TTIW1","TXPT2","ULAM6","UNLA2","UPBC1","VAKF1","VCAF1","VCAT2","VCVA2","VDZA2","VENF1","VERV4","VQSP4","WAHV2","WAKP8","WAQM3","WASD2","WATS1","WAXM3","WBYA1","WDEL1","WDSV2","WEBM1","WELM1","WEQM1","WEXM1","WHRI2","WIWF1","WKQA1","WKXA1","WLON7","WNEM4","WPLF1","WPOW1","WPTW1","WRBF1","WSLM4","WWEF1","WYCM6","YABP4","YATA2","YGNN6","YKRV2","YKTV2","YRSV2",

    };

    //array with all info
    private static final String[] FULLARRAY = new String[]{
            "42T58, 210 NM SSE of Kingston, Jamaica (SCP18) ",
            "51WH0, WHOTS, Woods Hole Ocean Time,series ",
            "21598, Drifter ",
            "32012, Woods Hole Stratus Wave Station ",
            "41002, SOUTH HATTERAS, 225 NM South of Cape Hatteras ",
            "41004, EDISTO, 41 NM Southeast of Charleston, SC ",
            "41008, GRAYS REEF, 40 NM Southeast of Savannah, GA ",
            "41009, CANAVERAL 20 NM East of Cape Canaveral, FL ",
            "41010, CANAVERAL EAST, 120NM East of Cape Canaveral ",
            "41013, Frying Pan Shoals, NC ",
            "41024, Sunset Nearshore (SUN 2) ",
            "41025, Diamond Shoals, NC ",
            "41029, Capers Nearshore (CAP 2) ",
            "41033, Fripp Nearshore (FRP 2) ",
            "41037, ILM3, 27 miles SE of Wrightsville Beach, NC ",
            "41038, ILM2, 5 miles SE of Wrightsville Beach, NC ",
            "41040, NORTH EQUATORIAL ONE, 470 NM East of Martinique ",
            "41041, NORTH EQUATORIAL TWO, 890 NM East of Martinique ",
            "41043, NE PUERTO RICO, 170 NM NNE of San Juan, PR ",
            "41044, NE ST MARTIN, 330 NM NE St Martin Is ",
            "41046, EAST BAHAMAS, 335 NM East of San Salvador Is,  Bahamas ",
            "41047, NE BAHAMAS, 350 NM ENE of Nassau, Bahamas ",
            "41048, WEST BERMUDA, 240 NM West of Bermuda ",
            "41049, SOUTH BERMUDA, 300 NM SSE of Bermuda ",
            "41051, South of St. Thomas, VI ",
            "41052, South of St. John, Virgin Islands ",
            "41053, San Juan, PR ",
            "41056, Vieques Island, PR ",
            "41060, Woods Hole Northwest Tropical Atlantic Wave Station ",
            "41064, Onslow Bay Outer, NC ",
            "41108, Wilmington Harbor, NC, (200) ",
            "41110, Masonboro Inlet, ILM2, NC (150) ",
            "41112, Offshore Fernandina Beach, FL (132) ",
            "41113, Cape Canaveral Nearshore, FL (143) ",
            "41114, Fort Pierce, FL (134) ",
            "41159, Onslow Bay Outer Waverider, NC (217) ",
            "42001, MID GULF, 180 nm South of Southwest Pass, LA ",
            "42002, WEST GULF, 207 NM East of Brownsville, TX ",
            "42003, East GULF, 208 NM West of Naples, FL ",
            "42012, ORANGE BEACH, 44 NM SE of Mobile, AL ",
            "42013, C10, WFS Central Buoy, 25m Isobath ",
            "42019, FREEPORT, TX, 60 NM South of Freeport, TX ",
            "42020, CORPUS CHRISTI, TX, 60NM SSE of Corpus Christi, TX ",
            "42022, C12, WFS Central Buoy, 50m Isobath ",
            "42023, C13, WFS South Buoy, 50m Isobath ",
            "42035, GALVESTON,TX,  22 NM East of Galveston, TX ",
            "42036, WEST TAMPA , 112 NM WNW of Tampa, FL ",
            "42039, PENSACOLA, 115NM SSE of Pensacola, FL ",
            "42040, LUKE OFFSHORE TEST PLATFORM, 63 NM South of Dauphin Island, AL ",
            "42044, PS,1126 TABS J ",
            "42046, HI,A595 TABS N ",
            "42047, HI,A389 TABS V ",
            "42055, BAY OF CAMPECHE, 214 NM NE OF Veracruz, MX ",
            "42056, Yucatan Basin, 120 NM ESE of Cozumel, MX ",
            "42057, Western Caribbean, 195 NM WSW of Negril Jamaica ",
            "42058, Central Caribbean, 210 NM SSE of Kingston, Jamaica ",
            "42059, Eastern Caribbean Sea, 180 NM SSW of Ponce, PR ",
            "42060, Caribbean Valley, 63 NM WSW of Montserrat ",
            "42067, USM3M01 ",
            "42085, Southeast of Ponce, PR ",
            "42087, Buccoo Reef, Tobago (West) ",
            "42088, Angel's Reef, Tobago (East) ",
            "42098, Egmont Channel Entrance, FL (214) ",
            "42099, Offshore St. Petersburg, FL (144) ",
            "42360, BW Pioneer buoy, C16471, Walker Ridge 249 ",
            "42361, Auger, Garden Banks 426 ",
            "42363, Mars B, Mississippi Canyon 807 ",
            "42365, Ursa TLP, Mississippi Canyon 809 ",
            "42390, Perdido, Alaminos Canyon 857 ",
            "42392, Atlantis Semisub, Green Canyon 787 ",
            "42394, Olympus TLP, Mississippi Canyon 807 ",
            "42887, Thunder Horse Semisub, Mississippi Canyon 778 ",
            "44005, GULF OF MAINE, 78 NM East of Portsmouth, NH ",
            "44007, PORTLAND 12 NM Southeast of Portland,ME ",
            "44008, NANTUCKET 54NM Southeast of Nantucket ",
            "44009, DELAWARE BAY 26 NM Southeast of Cape May, NJ ",
            "44011, GEORGES BANK 170 NM East of Hyannis, MA ",
            "44013, BOSTON 16 NM East of Boston, MA ",
            "44014, VIRGINIA BEACH 64 NM East of Virginia Beach, VA ",
            "44017, MONTAUK POINT, 23 NM SSW of Montauk Point, NY ",
            "44018, CAPE COD, 24 NM East of Provincetown, MA ",
            "44020, NANTUCKET SOUND ",
            "44024, Buoy N01, Northeast Channel ",
            "44025, LONG ISLAND, 30 NM South of Islip, NY ",
            "44027, Jonesport, ME, 20 NM SE of Jonesport, ME ",
            "44029, Buoy A01, Mass. Bay/Stellwagen ",
            "44030, Buoy B01, Western Maine Shelf ",
            "44032, Buoy E01, Central Maine Shelf ",
            "44033, Buoy F01, West Penobscot Bay ",
            "44034, Buoy I01, Eastern Maine Shelf ",
            "44037, Buoy M01, Jordan Basin ",
            "44039, Central Long Island Sound ",
            "44041, Jamestown, VA ",
            "44042, Potomac, MD ",
            "44043, Patapsco, MD ",
            "44056, Duck FRF, NC ",
            "44057, Susquehanna, MD ",
            "44058, Stingray Point, VA ",
            "44060, Eastern Long Island Sound ",
            "44061, Upper Potomac, MD ",
            "44062, Gooses Reef, MD ",
            "44063, Annapolis ",
            "44064, First Landing ",
            "44065, New York Harbor Entrance, 15 NM SE of Breezy Point, NY ",
            "44066, Texas Tower #4, 75 NM East of Long Beach, NJ ",
            "44069, Great South Bay ",
            "44090, Cape Cod Bay, MA (221) ",
            "44091, Barnegat, NJ (209) ",
            "44093, Offshore Wind Energy Area, VA (210) ",
            "44095, Oregon Inlet, NC (192) ",
            "44096, Cape Charles, VA (186) ",
            "44097, Block Island, RI  (154) ",
            "44098, Jeffrey's Ledge, NH (160) ",
            "44099, Cape Henry, VA (147) ",
            "44137, East Scotia Slope ",
            "44139, Banqureau Banks ",
            "44141, Laurentian Fan ",
            "44150, La Have Bank ",
            "44251, Nickerson Bank ",
            "44255, NE Burgeo Bank ",
            "44258, Halifax Harbour ",
            "45001, MID SUPERIOR, 60NM North Northeast Hancock, MI ",
            "45002, NORTH MICHIGAN, Halfway between North Manitou and Washington Islands. ",
            "45003, NORTH HURON, 32NM Northeast of Alpena, MI ",
            "45004, EAST SUPERIOR,70 NM NE Marquette, MI ",
            "45005, WEST ERIE, 16 NM NW of Lorain, OH ",
            "45006, WEST SUPERIOR, 30NM NE of Outer Island, WI ",
            "45007, SOUTH MICHIGAN, 43NM East Southeast of Milwaukee, WI ",
            "45008, SOUTH HURON, 43NM East of Oscoda, MI ",
            "45012, EAST Lake Ontario , 20NM North Northeast of Rochester, NY ",
            "45013, Atwater Park, WI ",
            "45014, Central Green Bay, WI ",
            "45022, Little Traverse Bay, MI ",
            "45023, Portage Canal ",
            "45024, Ludington, MI ",
            "45025, South Entrance to Keweenaw Waterway, MI ",
            "45026, St. Joseph, MI ",
            "45027, North of Duluth, MN ",
            "45028, Western Lake Superior ",
            "45029, Holland, MI ",
            "45132, Port Stanley ",
            "45135, Prince Edward Pt ",
            "45136, Slate Island ",
            "45137, Georgian Bay ",
            "45138, Mount Louis ",
            "45139, West Lake Ontario, Grimsby ",
            "45140, Lake Winnipeg S. Basin ",
            "45142, Port Colborne ",
            "45143, South Georgian Bay ",
            "45145, Lake Winnipeg Narrows ",
            "45147, Lake St Clair ",
            "45148, Lake of the Woods ",
            "45149, Southern Lake Huron ",
            "45152, Lake Nipissing ",
            "45154, North Channel East ",
            "45159, NW Lake Ontario Ajax ",
            "45161, Muskegon, MI ",
            "45162, Alpena, MI ",
            "45163, Saginaw Bay, MI ",
            "45164, Cleveland, OH ",
            "45165, Oregon, OH ",
            "45167, Erie, PA ",
            "45168, South Haven, MI ",
            "45169, Lakewood, OH ",
            "45170, Michigan City, Indiana ",
            "45171, Granite Island Buoy ",
            "45174, Wilmette, IL ",
            "45175, Mackinac Straits, MI ",
            "45176, Cleveland Crib, OH ",
            "46001, WESTERN GULF OF ALASKA , 175NM SE of Kodiak, AK ",
            "46002, WEST OREGON, 275NM West of Coos Bay, OR ",
            "46004, Middle Nomad ",
            "46005, WEST WASHINGTON, 300NM West of Aberdeen, WA ",
            "46011, SANTA MARIA, 21NM NW of Point Arguello, CA ",
            "46012, HALF MOON BAY, 24NM SSW of San Francisco, CA ",
            "46013, BODEGA BAY, 48NM NW of San Francisco, CA ",
            "46014, PT ARENA, 19NM North of Point Arena, CA ",
            "46015, PORT ORFORD, 15 NM West of Port Orford, OR ",
            "46022, EEL RIVER, 17NM WSW of Eureka, CA ",
            "46025, Santa Monica Basin, 33NM WSW of Santa Monica, CA ",
            "46026, SAN FRANCISCO, 18NM West of San Francisco, CA ",
            "46027, ST GEORGES, 8NM NW of Crescent City, CA ",
            "46028, CAPE SAN MARTIN, 55NM West NW of Morro Bay, CA ",
            "46029, COLUMBIA RIVER BAR, 20NM West of Columbia River Mouth ",
            "46035, CENTRAL BERING SEA, 310 NM North of Adak, AK ",
            "46036, South Nomad ",
            "46041, CAPE ELIZABETH, 45NM NW of Aberdeen, WA ",
            "46042, MONTEREY, 27NM WNW of Monterey, CA ",
            "46047, TANNER BANK, 121NM West of San Diego, CA ",
            "46050, STONEWALL BANK, 20NM West of Newport, OR ",
            "46053, EAST SANTA BARBARA , 12NM Southwest of Santa Barbara, CA ",
            "46054, WEST SANTA BARBARA  38 NM West of Santa Barbara, CA ",
            "46059, WEST CALIFORNIA, 357NM West of San Francisco, CA ",
            "46061, Seal Rocks, Between Montague and Hinchinbrook Islands, AK ",
            "46066, SOUTH KODIAK, 310NM SSW of Kodiak, AK ",
            "46069, SOUTH SANTA ROSA IS. CA ",
            "46070, SOUTHWEST BERING SEA, 142NM NNE OF ATTU IS, AK ",
            "46071, WESTERN ALEUTIANS, 14NM SOUTH OF AMCHITKA IS, AK ",
            "46072, CENTRAL ALEUTIANS 230NM SW Dutch Harbor ",
            "46076, CAPE CLEARE, 17 NM South of Montague Is,  AK ",
            "46080, PORTLOCK BANK, 76NM ENE of Kodiak, AK ",
            "46081, Western Prince William Sound ",
            "46083, Fairweather Ground 105NM West  of Juneau, AK ",
            "46084, Cape Edgecumbe, 25NM SSW of Cape Edgecumbe, AK ",
            "46085, CENTRAL GULF OF ALASKA,  265NM West of Cape Ommaney, AK ",
            "46086, SAN CLEMENTE BASIN, 27NM SE Of San Clemente Is, CA ",
            "46087, Neah Bay, 6NM North of Cape Flattery, WA     (Traffic Separation Lighted Buoy) ",
            "46088, New Dungeness, 17 NM NE of Port Angeles, WA ",
            "46089, Tillamook, OR, 85 NM WNW of Tillamook, OR ",
            "46092, MBM1 ",
            "46108, Lower Cook Inlet (204) ",
            "46114, West Monterey Bay, CA (185) ",
            "46118, Se'lhaem Bellingham Bay ",
            "46120, Pt Wells, WA (U of Wash) ",
            "46121, Carr Inlet, WA (U of Wash) ",
            "46123, Twanoh, Hood Canal, WA (U of Wash) ",
            "46125, Hansville, Hood Canal, WA ",
            "46131, Sentry Shoal ",
            "46132, South Brooks ",
            "46134, Pat Bay ",
            "46145, Central Dixon Entrance Buoy ",
            "46146, Halibut Bank ",
            "46147, South Moresby ",
            "46181, Nanakwa Shoal ",
            "46183, North Hecate Strait ",
            "46184, North Nomad ",
            "46185, South Hecate Strait ",
            "46204, West Sea Otter ",
            "46205, West Dixon Entrance ",
            "46206, La Perouse Bank ",
            "46207, East Dellwood ",
            "46208, West Moresby ",
            "46211, Grays Harbor, WA (036) ",
            "46213, Cape Mendocino, CA (094) ",
            "46214, Point Reyes, CA (029) ",
            "46215, Diablo Canyon, CA (076) ",
            "46216, Goleta Point, CA (107) ",
            "46217, Anacapa Passage, CA (111) ",
            "46218, Harvest, CA (071) ",
            "46219, San Nicolas Island, CA (067) ",
            "46221, Santa Monica Bay, CA (028) ",
            "46222, San Pedro, CA (092) ",
            "46224, Oceanside Offshore, CA (045) ",
            "46225, Torrey Pines Outer, CA (100) ",
            "46229, UMPQUA OFFSHORE, OR (139) ",
            "46232, Point Loma South, CA  (191) ",
            "46237, San Francisco Bar, CA  (142) ",
            "46239, Point Sur, CA (157) ",
            "46240, Cabrillo Point, Monterey Bay, CA  (158) ",
            "46242, Camp Pendleton Nearshore, CA  (043) ",
            "46243, Clatsop Spit, OR (162) ",
            "46244, Humboldt Bay, North Spit, CA (168) ",
            "46246, Ocean Station PAPA  (166) ",
            "46248, Astoria Canyon, OR  (179) ",
            "46251, Santa Cruz Basin, CA (203) ",
            "46253, San Pedro South, CA (213) ",
            "46254, SCRIPPS Nearshore, CA (201) ",
            "46255, Begg Rock, CA  (138) ",
            "46256, Long Beach Channel, CA (215) ",
            "46257, Harvest Southeast, CA (216) ",
            "46258, Mission Bay West, CA (220) ",
            "46259, Santa Lucia Escarpment, CA (222) ",
            "51000, NORTHERN HAWAII ONE, 245NM NE of Honolulu HI ",
            "51001, NORTHWESTERN HAWAII ONE, 170 NM West Northwest of Kauai Island ",
            "51002, SOUTHWEST HAWAII, 215NM SSW of Hilo, HI ",
            "51003, WESTERN  HAWAII, 205 NM SW of Honolulu, HI ",
            "51004, SOUTHEAST HAWAII, 205 NM Southeast of Hilo, HI ",
            "51101, NORTHWESTERN HAWAII TWO, 190NM NW of Kauai Is, HI  ",
            "51201, Waimea Bay, HI (106) ",
            "51202, Mokapu Point, HI (098) ",
            "51203, Kaumalapau, HI (146) ",
            "51204, Barbers Point, HI #2 (165) ",
            "51205, Pauwela, Maui, HI (187) ",
            "51206, Hilo, Hawaii, HI (188) ",
            "51207, Kaneohe Bay, HI (198) ",
            "51209, Aunuu, American Samoa (189) ",
            "52200, Ipan, Guam (121) ",
            "52201, Kalo, Majuro, Marshall Islands (163) ",
            "52211, Tanapag, Saipan, NMI (197) ",
            "62001, Gascogne Buoy ",
            "62027, Jersey Buoy, English Channel, 5 nm south of Jersey, UK ",
            "62029, K1 Buoy ",
            "62030, L4 Buoy ",
            "62050, E1 ",
            "62081, K2 Buoy ",
            "62095, M6, West Coast ",
            "62102, Armada AWS ",
            "62103, Channel Lightship ",
            "62104, Montrose ",
            "62105, K4 Buoy ",
            "62107, Sevenstones Lightship ",
            "62111, Goldeneye AWS ",
            "62112, Claymore AWS ",
            "62113, Piper ",
            "62114, Tartan A AWS ",
            "62115, Beatrice A ",
            "62116, Nelson AWS ",
            "62117, Buchan A ",
            "62118, Forties ",
            "62119, Shearwater AWS ",
            "62120, Fulmar ",
            "62121, Carrack AWS ",
            "62122, Etap AWS ",
            "62123, Janice A ",
            "62124, Conwy ",
            "62127, Cleeton AWS ",
            "62128, Miller AWS ",
            "62129, Saltire AWS ",
            "62130, Brae A ",
            "62131, Babbage AWS ",
            "62132, Auk ",
            "62133, Gannet AWS ",
            "62134, Andrew AWS ",
            "62135, Trent ",
            "62136, Katy ",
            "62137, Galaxy 1 ",
            "62138, Rough ",
            "62139, Loggs ",
            "62140, Transocean Rig 140 ",
            "62143, North Everest AWS ",
            "62144, Clipper AWS ",
            "62145, North Sea ",
            "62146, Lomond AWS ",
            "62148, Barque AWS ",
            "62149, West Sole A AWS ",
            "62150, Amethyst AWS ",
            "62151, Jade ",
            "62152, Elgin AWS ",
            "62153, Mungo AWS ",
            "62154, Clyde AWS ",
            "62155, Unity AWS ",
            "62157, Scott ",
            "62160, Judy ",
            "62161, Tiffany ",
            "62162, Kittiwake ",
            "62163, Brittany Buoy ",
            "62164, Anasuria AWS ",
            "62165, Ravenspurn North AWS ",
            "62167, Viking Bravo ",
            "62168, Britannia ",
            "62170, F3 Light Vessel ",
            "62296, Inde 23A ",
            "62297, East Brae ",
            "62302, Leman 27A ",
            "62304, Sandettie Lightship ",
            "62305, Greenwich Lightship ",
            "63055, Dunbar AWS ",
            "63056, Bruce AWS ",
            "63057, Harding AWS ",
            "63058, Clair Ridge ",
            "63059, Buzzard ",
            "63101, Tern ",
            "63102, Ninian Central ",
            "63103, North Cormorant AWS ",
            "63104, Dunlin ",
            "63105, Brent B AWS ",
            "63106, Brent ",
            "63107, Brent ",
            "63108, North Alwyn AWS ",
            "63109,  ",
            "63110, Beryl A AWS ",
            "63111, Thistle Alpha ",
            "63112, Cormorant AWS ",
            "63113, Brent A AWS ",
            "63115, Magnus AWS ",
            "63117, Eider AWS ",
            "63118, Paul B. Lloyd Jr. ",
            "63120, Murdoch ",
            "64041, Clair AWS ",
            "64045, K5 Buoy ",
            "64046, K7 Buoy ",
            "AAMC1, 9414750, Alameda, CA ",
            "ACXS1, Bennett's Point, ACE Basin Reserve, SC ",
            "ACYN4, 8534720, Atlantic City, NJ ",
            "ADKA2, 9461380, Adak Island, AK ",
            "AGCM4, 9014070, Algonac, MI ",
            "AGMW3, Algoma City Marina WI ",
            "ALIA2, 9457804, Alitak, AK ",
            "ALXN6, 8311062, Alexandria Bay, NY ",
            "AMAA2, East Amatuli Island Light, AK ",
            "AMRL1, 8764227, Amerada Pass, LA ",
            "ANMN6, Field Station, Hudson River Reserve, NY ",
            "ANTA2, 9455920, Anchorage, AK ",
            "ANVC1, 9416841, Arena Cove, CA ",
            "APAM2, 8575512, Annapolis, MD ",
            "APCF1, 8728690, Apalachicola, FL ",
            "APNM4, Alpena Harbor Light, MI ",
            "APRP7, 1630000, Apra Harbor, Guam ",
            "APXF1, East Bay, Apalachicola Reserve, FL ",
            "AROP4, 9757809, Arecibo, PR ",
            "ARPF1, APK, Aripeka, FL ",
            "ASTO3, 9439040, Astoria, OR ",
            "ATGM1, 8413320, Bar Harbor, ME ",
            "ATKA2, 9461710, Atka, AK ",
            "AUGA2, Augustine Island, AK ",
            "AWRT2, 8774230, Aransas Wildlife Refuge, TX ",
            "BABT2, 8776604, Baffin Bay; Point of Rocks, TX ",
            "BARA9, 9761115, Barbuda, Barbuda ",
            "BATN6, 8518750, The Battery, NY ",
            "BDRN4, 8539094, Burlington, Delaware River, NJ ",
            "BDSP1, 8546252, Bridesburg, PA ",
            "BDVF1, Broad River, FL ",
            "BDXC1, Bodega, CA ",
            "BEPB6, 2695540, Bermuda Esso Pier ",
            "BFTN7, 8656483, Beaufort, NC ",
            "BGCF1, Big Carlos Pass, FL ",
            "BGNN4, 8519483, Bergen Point West Reach, NY ",
            "BGXN3, Greenland, Great Bay Reserve, NH ",
            "BHBM3, 8443970, Boston, MA ",
            "BHRI3, Burns Harbor, IN ",
            "BIGM4, Big Bay, MI ",
            "BISM2, 8571421, Bishops Head, MD ",
            "BKBF1, 8720357,  I,295 Bridge, St Johns River, FL ",
            "BKTL1, 8767961, Lake Charles Bulk Terminal, LA ",
            "BKYF1, Buoy Key, FL ",
            "BLIA2, Bligh Reef Light, AK ",
            "BLIF1, 8720233, Blount Island Command, St Johns River, FL ",
            "BLTM2, 8574680, Baltimore, MD ",
            "BLTM3, 8447387, Borden Flats Light at Fall River, MA ",
            "BNKF1, Butternut Key, FL ",
            "BOBF1, Bob Allen, FL ",
            "BRHC3, 8467150, Bridgeport, CT ",
            "BRND1, 8555889, Brandywine Shoal Light, DE ",
            "BSBM4, Big Sable Point, MI ",
            "BSCA1, Bon Secour, AL ",
            "BSLM2, Jug Bay, Chesapeake Bay, MD ",
            "BUFN6, 9063020, Buffalo, NY ",
            "BURL1, Southwest Pass, LA ",
            "BUZM3, Buzzards Bay, MA ",
            "BWSF1, Blackwater Sound, FL ",
            "BYGL1, 8762482, West Bank 1, Bayou Gauche, LA ",
            "BZBM3, 8447930, Woods Hole, MA ",
            "CAMM2, 8571892, Cambridge, MD ",
            "CANF1, Cane Patch, FL ",
            "CAPL1, 8768094, Calcasieu Pass, LA ",
            "CARL1, 8761955, Carrollton, LA ",
            "CASM1, 8418150, Portland, ME ",
            "CBBV2, 8638863, Chesapeake Bay Bridge Tunnel, VA ",
            "CBLO1, Conneaut Breakwater Light, OH ",
            "CBRW3, Chambers Island, WI ",
            "CDEA2, Cape Decision, AK ",
            "CDRF1, Cedar Key, FL ",
            "CECC1, 9419750, Crescent City, CA ",
            "CFWM1, 8411060, Cutler Farris Wharf, ME ",
            "CHAO3, 9432780, Charleston, OR ",
            "CHAV3, 9751639, Charlotte Amalie, VI ",
            "CHCM2, 8573927, Chesapeake City, MD ",
            "CHII2, Chicago, IL ",
            "CHLV2, Chesapeake Light, VA ",
            "CHSV3, 9751364, Christiansted Harbor, Virgin Islands ",
            "CHTS1, 8665530, Charleston, SC ",
            "CHYV2, 8638999, Cape Henry, VA ",
            "CHYW1, 9449424, Cherry Point, WA ",
            "CLBP4, 9752235, Culebra, PR ",
            "CLKN7, Cape Lookout, NC ",
            "CLSM4, St. Clair Shores, MI ",
            "CMAN4, 8536110, Cape May, NJ ",
            "CMTI2, 9087044, Calumet Harbor, IL ",
            "CNBF1, Cannon Bay, FL ",
            "CNDO1, 9063063, Cleveland, OH ",
            "CNII2, Northerly Island, IL ",
            "COVM2, 8577018, Cove Point LNG Pier, MD ",
            "CPMW1, 9449419, Cherry Point South Dock Met, WA ",
            "CPNT2, 8774513, Copano Bay, TX ",
            "CPTR1, 8452944, Conimicut Light, RI ",
            "CPVM2, 8575437, Chesapeake Bay Visibility ",
            "CPXC1, Cal Poly Pier, CA ",
            "CQUC1, Carquinez, CA ",
            "CRTA1, Cedar Point, AL ",
            "CRVA2, 9454050, Cordova, AK ",
            "CSPA2, Cape Spencer, AK ",
            "CWAF1, Clear Water Pass, FL ",
            "CWBF1, 8726724, Clearwater Beach, FL ",
            "DBLN6, Dunkirk, NY ",
            "DELD1, 8551762, Delaware City, DE ",
            "DESW1, Destruction Island, WA ",
            "DISW3, Devils Island, WI ",
            "DKCM6, 8741501, Dock C, Pascagoula, MS ",
            "DKKF1, Duck Key, FL ",
            "DMSF1, 8720219, Dames Point, FL ",
            "DOMV2, 8638511, Dominion Terminal Associates, VA ",
            "DPIA1, Dauphin Island, AL ",
            "DPXC1, 9415141, Davis Point, San Pablo Bay, CA ",
            "DRFA2, Drift River Terminal, AK ",
            "DRSD1, Saint Jones River, Delaware Reserve, DE ",
            "DTLM4, 9075099, De Tour Village, MI ",
            "DUKN7, 8651370, Duck Pier, NC ",
            "DULM5, 9099064, Duluth, MN ",
            "EBSW1, 9447130, Seattle, WA ",
            "EINL1, 8764314, North of Eugene Island, LA ",
            "ELFA2, 9452634, Elfin Cove, AK ",
            "ELXC1, Elkhorn Slough Reserve, CA ",
            "EPTT2, 8771013, Eagle Point, TX ",
            "EREP1, 9063038, Erie ",
            "EROA2, Eldred Rock, AK ",
            "ESPP4, 9752695, Esperanza, PR ",
            "FAIO1, 9063053, Fairport, OH ",
            "FBIS1, Folly Island, SC ",
            "FCGT2, 8772447, USCG Freeport, TX ",
            "FFIA2, Five Fingers, AK ",
            "FHPF1, Fred Howard Park, FL ",
            "FILA2, Flat Island Light, AK ",
            "FMOA1, 8734673, Fort Morgan, AL ",
            "FMRF1, 8725520, Fort Myers, FL ",
            "FOXR1, 8454000, Providence, RI ",
            "FPKG1, 8670870, Fort Pulaski, GA ",
            "FPTM4, Fairport, MI ",
            "FPXC1, Fort Point, CA ",
            "FRDF1, 8720030, Fernandina Beach, FL ",
            "FRDP4, 9753216, Fajardo, PR ",
            "FRDW1, 9449880, Friday Harbor, WA ",
            "FREL1, 8762484, Frenier Landing, LA ",
            "FRVM3, 8447386, Fall River, MA ",
            "FRWL1, 8766072, Fresh Water Canal Locks, LA ",
            "FRXM3, 8447412, Fall River Visibility, MA ",
            "FSKM2, 8574728, Francis Scott Key Bridge, MD ",
            "FSNM2, 8574729, Francis Scott Key Bridge NE Tower, MD ",
            "FSTI2, Foster Ave Chicago, IL (CPD) ",
            "FTGM4, 9014098, Fort Gratiot, MI ",
            "FTPC1, 9414290, San Francisco, CA ",
            "FWYF1, Fowey Rock, FL ",
            "GBIF1, Gunboat Island, FL ",
            "GBTF1, Garfield Bight, FL ",
            "GCVF1, 8720503, Red Bay Point, FL ",
            "GDMM5, 9099090, Grand Marais, MN ",
            "GDXM6, Crooked Bayou, Grand Bay Reserve, MS ",
            "GELO1, Geneva on the Lake, OH ",
            "GISL1, 8761724, Grand Isle, LA ",
            "GKYF1, Garden Key, FL ",
            "GNJT2, 8771341, Galveston Bay (North Jetty), TX ",
            "GRBL1, Grand Isle Blocks, LA / CSI09 ",
            "GRIM4, Granite Island, MI ",
            "GSLM4, Gravelly Shoals Light MI ",
            "GTLM4, Grand Traverse Light, MI ",
            "GTOT2, 8771450, Galveston Pier 21, TX ",
            "GTRM4, Superior Grand Traverse Bay, MI ",
            "HBXC1, Humboldt, CA ",
            "HBYC1, 9418767, North Spit, CA ",
            "HCEF1, Highway Creek, FL ",
            "HCGN7, 8654467, USCG Hatteras, NC ",
            "HIST2, 8770808, High Island, TX ",
            "HLNM4, 9087031, Holland, MI ",
            "HMRA2, Homer, Kachemak Bay Reserve, AK ",
            "HMSA2, Homer Spit, AK ",
            "HRBM4, 9075014, Harbor Beach, MI ",
            "HREF1, Harney River, FL ",
            "HRVC1, 9411406, Harvest Oil Platform, CA ",
            "ICAC1, 9410840, Santa Monica Pier ",
            "ICYA2, Icy Bay, AK ",
            "IIWC1, 9410172, USS Midway South Navy Pier, San Diego, CA ",
            "ILOH1, 1617760, Hilo, HI ",
            "IMGP4, Isla Magueyes, Lajas, PR ",
            "IOSN3, Isle of Shoals, NH ",
            "ITKA2, 9451600, Sitka, AK ",
            "JAKI2, 63rd ST, Chicago, IL (CPD) ",
            "JCRN4, Nacote Creek, Jacques Cousteau Reserve, NJ ",
            "JKYF1, Johnson Key, FL ",
            "JMPN7, 8658163, Johnny Mercer Pier, Wrightsville Beach, NC ",
            "JNEA2, 9452210, Juneau, AK ",
            "JOXP4, Jobos Bay Reserve, Puerto Rico ",
            "JXUF1, 8720245, Jacksonville University, FL ",
            "KATA1, Katrina Cut, AL ",
            "KDAA2, 9457292, Kodiak Island, AK ",
            "KECA2, 9450460, Ketchikan, AK ",
            "KGCA2, 9459881, King Cove, AK ",
            "KLIH1, 1615680, Kahului, Kahului Harbor, HI ",
            "KNSW3, Kenosha, WI ",
            "KPTN6, 8516945, Kings Point, NY ",
            "KPTV2, 8632200, Kiptopeke, VA ",
            "KTNF1, Keaton Beach, FL ",
            "KWHH1, 1617433, Kawaihae, HI ",
            "KWJP8, 1820000, Kwajalein, Marshall Islands ",
            "KWNW3, 9087069, Kewaunee MET, WI ",
            "KYWF1, 8724580, Key West, FL ",
            "LAMV3, 9751381, Lameshur Bay, St John, VI ",
            "LAPW1, 9442396, La Push, WA ",
            "LBRF1, Broad River Lower, FL ",
            "LCLL1, 8767816, Lake Charles, LA ",
            "LCNA2, Lincoln Island, AK ",
            "LDLC3, New London Ledge CT, Ledge Light Weather Station ",
            "LDTM4, 9087023, Ludington, MI ",
            "LJAC1, 9410230, La Jolla, CA ",
            "LJPC1, La Jolla, CA (073) ",
            "LKWF1, 8722670, Lake Worth Pier, FL ",
            "LMBV4, La Mancha Beach, Mexico ",
            "LMDF1, Little Madeira, FL ",
            "LMFS1, Lake Murray SC ",
            "LMRF1, Lostmans River, FL ",
            "LNDC1, 9414763, Oakland Berth 67, CA ",
            "LONF1, Long Key, FL ",
            "LOPL1, Louisiana Offshore Oil Port, LA ",
            "LOPW1, 9440422, Longview, WA ",
            "LPNM4, 9075065, Alpena, MI ",
            "LRIF1, Lane River, FL ",
            "LRKF1, Little Rabbit Key, FL ",
            "LSNF1, Long Sound, FL ",
            "LTJF1, 8720228, Little Jetties, St. Johns River, FL ",
            "LTRM4, 9076033, Little Rapids, MI ",
            "LUIT2, 8771972, San Luis Pass, TX ",
            "LWSD1, 8557380, Lewes, DE ",
            "LWTV2, 8635750, Lewisetta, VA ",
            "LYBT2, 8770733, Lynchburg Landing, TX ",
            "MACM4, 9075080, Mackinaw City, MI ",
            "MAXT2, Copano East, Mission,Aransas Reserve, TX ",
            "MBLA1, Middle Bay Light, AL ",
            "MBRM4, 9014090, Mouth of the Black River, MI ",
            "MBXC1, Morro Bay, T Pier, CA ",
            "MCGA1, 8736897, Coast Guard Sector Mobile, AL ",
            "MCGM4, 9099018, Marquette C.G., MI ",
            "MCYF1, 8726667, McKay Bay Entrance, FL ",
            "MCYI3, Michigan City, IN ",
            "MDRM1, Mt Desert Rock, ME ",
            "MEEM4, Manistee Harbor, MI ",
            "MGIP4, 9759110, Magueyes Islands, PR ",
            "MGPT2, 8770613, Morgans Point, TX ",
            "MGZP4, 9759394, Mayaguez, PR ",
            "MHPA1, Meaher Park, AL ",
            "MHRN6, 8519532, Mariners Harbor, NY ",
            "MISM1, Matinicus Rock, ME ",
            "MISP4, 9759938, Mona Island, PR ",
            "MIST2, Aransas Ship Channel ",
            "MKGM4, Muskegon, MI ",
            "MLRF1, Molasses Reef, FL ",
            "MLSC1, Moss Landing, South Harbor, CA ",
            "MNMM4, 9087088, Menominee, MI ",
            "MNPV2, 8639348, Money Point, VA ",
            "MOKH1, 1612480, Mokuoloe, HI ",
            "MQTT2, 8775870, Bob Hall Pier, Corpus Christi, TX ",
            "MRHO1, 9063079, Marblehead, OH ",
            "MRKA2, Middle Rock Light, AK ",
            "MRNA2, Marmion Island, AK ",
            "MROS1, 8661070, Springmaid Pier, SC ",
            "MRYA2, Mary Island, AK ",
            "MTBF1, 8726412, Middle Tampa Bay ",
            "MTKN6, 8510560, Montauk, NY ",
            "MTYC1, 9413450, Monterey, CA ",
            "MUKF1, Murray Key, FL ",
            "MYPF1, 8720218, Mayport (Bar Pilots Dock), FL ",
            "MZXC1, 9415102, Martinez,Amorco Pier, CA ",
            "NABM4, Naubinway, MI ",
            "NAXR1, Potters Cove, Narragansett Bay Reserve, RI ",
            "NBLP1, 8548989, Newbold, PA ",
            "NCDV2, 8635027, Dahlgren, VA ",
            "NCHT2, 8770777, Manchester, TX ",
            "NEAW1, 9443090, Neah Bay, WA ",
            "NFDF1, 8720215, Navy Fuel Depot, St Johns River, FL ",
            "NIAN6, 9063012, Niagara Intake, NY ",
            "NIWS1, Oyster Landing, North Inlet,Winyah Bay Reserve, SC ",
            "NKTA2, 9455760, Nikiski, AK ",
            "NLNC3, 8461490, New London, CT ",
            "NMTA2, 9468756, Nome, Norton Sound, AK ",
            "NOXN7, Research Creek, North Carolina Reserve, NC ",
            "NPDW3, Northport Pier at Death's Door WI ",
            "NPSF1, 8725110, Naples, FL ",
            "NRRF1, North River, FL ",
            "NSTP6, 1770000, Pago Pago, American Samoa ",
            "NTBC1, 9411340, Santa Barbara, CA ",
            "NTKM3, 8449130, Nantucket Island, MA ",
            "NUET2, 8775244, Nueces Bay, TX ",
            "NWCL1, 8761927, New Canal, LA ",
            "NWHC3, 8465705, New Haven, CT ",
            "NWPO3, Newport, OR ",
            "NWPR1, 8452660, Newport, RI ",
            "NWWH1, 1611400, Nawiliwili, HI ",
            "OBGN6, 8311030, Ogdensburg, NY ",
            "OBLA1, 8737048, Mobile State Docks, AL ",
            "OBXC1, 9414797, Oakland Berth 38, CA ",
            "OCIM2, 8570283, Ocean City Inlet, MD ",
            "OHBC1, 9410660, Los Angeles, CA ",
            "OKSI2, Oak ST, Chicago, IL (CPD) ",
            "OKXC1, 9414776, Oakland Berth 34, CA ",
            "OLCN6, Olcott Harbor, NY ",
            "OLSA2, 9462450, Nikolski, AK ",
            "OOUH1, 1612340, Honolulu, HI ",
            "OPTF1, 8726607, Old Port Tampa, FL ",
            "ORIN7, 8652587, Oregon Inlet Marina, NC ",
            "OSGN6, 9052030, Oswego, NY ",
            "OSTF1, Stennis Test Facility ",
            "OVIA2, 9455500, Seldovia, AK ",
            "OWXO1, Old Woman Creek, OH ",
            "PACF1, 8729108, Panama City, FL ",
            "PACT2, 8775792, Packery Channel, TX ",
            "PBFW1, Padilla Bay Farm, Padilla Bay Reserve, WA ",
            "PBPA2, Point Bishop, AK ",
            "PCBF1, 8729210, Panama City Beach, FL ",
            "PCGT2, 8779748, South Padre Island CGS, TX ",
            "PCLF1, 8729840, Pensacola, FL ",
            "PCLM4, Portage Canal, MI ",
            "PCNT2, 8773701, Matagorda Bay; Port O'Connor, TX ",
            "PCOC1, 9415144, Port Chicago, CA ",
            "PFXC1, 9410670, Los Angeles Pier F, CA ",
            "PGBP7, 1631428, Pago Bay, Guam ",
            "PHBP1, 8545240, Philadelphia, PA ",
            "PILA2, Pilot Rock, AK ",
            "PILL1, 8760721, Pilottown, LA ",
            "PILM4, Passage Island, MI ",
            "PKBW3, Pokegama Bay, Lake Superior Reserve, WI ",
            "PKYF1, Peterson Key, FL ",
            "PLSF1, Pulaski Shoals Light, FL ",
            "PLXA2, 9451054, Port Alexander, AK ",
            "PMAF1, 8726384, Port Manatee, FL ",
            "PMNT2, 8778490, Port Mansfield, TX ",
            "PMOA2, 9463502, Port Moller, AK ",
            "PNGW3, Port Wing, WI ",
            "PNLM4, 9087096, Port Inland, MI ",
            "PORO3, 9431647, Port Orford, OR ",
            "PORT2, 8770475, Port Arthur, TX ",
            "POTA2, Potato Point, AK ",
            "PPTA1, Perdido Pass, AL ",
            "PPTM2, 8578240, Piney Point, MD ",
            "PPXC1, 9414847, Point Potrero, Richmond, CA ",
            "PRDA2, 9497645, Prudhoe Bay, AK ",
            "PRJC1, 9410665, Los Angeles Pier J, CA ",
            "PRTA2, Point Retreat, AK ",
            "PRUR1, 8452314, Sandy Point, Prudence Island ",
            "PRYC1, 9415020, Point Reyes, CA ",
            "PSBC1, 9415115, Pittsburg (Suisun Bay), CA ",
            "PSBM1, 8410140, Eastport, ME ",
            "PSCM4, Port Sanilac, MI ",
            "PSLC1, 9412110, Port San Luis, CA ",
            "PSTL1, 8760922, Pilot's Station East, SW Pass, LA ",
            "PSTN6, 9063028, Sturgeon Point, NY ",
            "PTAT2, Port Aransas, TX ",
            "PTAW1, 9444090, Port Angeles, WA ",
            "PTBM6, 8741003, Petit Bois Island, MS ",
            "PTCR1, 8452951, Potter Cove, Prudence Island, RI ",
            "PTIM4, 9099004, Point Iroquois, MI ",
            "PTIT2, 8779770, Port Isabel, TX ",
            "PTLA2, Portland Island, AK ",
            "PTOA1, 8737005, Pinto Island, AL ",
            "PTRP4, Puntas Rincon, PR ",
            "PTWW1, 9444900, Port Townsend, WA ",
            "PVGF1, Port Everglades Channel, FL ",
            "PWAW3, Port Washington, WI ",
            "PXOC1, 9414311, San Francisco Pier 1, CA ",
            "PXSC1, 9414296, Pier 17, San Francisco Bay, CA ",
            "QPTR1, 8454049, Quonset Point, RI ",
            "RARM6, 8741094, Range A rear, Pascagoula, MS ",
            "RCKM4, 9076024, Rock Cut, MI ",
            "RCMC1, 9414863, Richmond, CA ",
            "RCPT2, 8774770, Rockport, TX ",
            "RCRN6, 9052058, Rochester, NY ",
            "RCYF1, 8720625, Racy Point, St. Johns River, FL ",
            "RDDA2, 9491094, Red Dog Dock, AK ",
            "RDYD1, 8551910, Reedy Point, DE ",
            "RKXF1, Upper Henderson Creek, Rookery Bay Reserve, FL ",
            "RLIT2, 8779280, Realitos Peninsula, TX ",
            "RLOT2, 8770971, Rollover Pass, TX ",
            "ROAM4, Rock of Ages, MI ",
            "ROBN4, 8530973, Robbins Reef, NJ ",
            "RPLV2, 8632837, Rappahannock Light, VA ",
            "RPRN6, Rochester, NY ",
            "RSJT2, 8777812, Rincon del San Jose; Potrero Lopeno SW, TX ",
            "RTAT2, 8775237, Port Aransas, TX ",
            "RTYC1, 9414523, Redwood City, CA ",
            "SACV4, Sacrifice Island, Mexico ",
            "SANF1, Sand Key, FL ",
            "SAPF1, 8726520, St. Petersburg, FL ",
            "SAUF1, St. Augustine, FL ",
            "SAXG1, Marsh Island, Sapelo Island Reserve, GA ",
            "SBEO3, 9435380, South Beach, OR ",
            "SBIO1, South Bass Island, OH ",
            "SBLM4, Saginaw Bay Light #1, MI ",
            "SBPT2, 8770570, Sabine Pass North, TX ",
            "SDBC1, 9410170, San Diego, CA ",
            "SDHN4, 8531680, Sandy Hook, NJ ",
            "SDRT2, 8773037, Seadrift, TX ",
            "SFXC1, Rush Ranch, San Francisco Bay Reserve, CA ",
            "SGNW3, Sheboygan, WI ",
            "SGOF1, Tyndall AFB Tower C (N4), FL ",
            "SHBL1, 8761305, Shell Beach, LA ",
            "SHPF1, SHP, Shell Point, FL ",
            "SIPF1, Sebastian Inlet State Park, FL ",
            "SISA2, Sisters Island, AK ",
            "SISW1, Smith Island, WA ",
            "SJNP4, 9755371, San Juan, PR ",
            "SJOM4, St. Joseph, MI ",
            "SJSN4, 8537121, Ship John Shoal, NJ ",
            "SKTA2, 9452400, Skagway, AK ",
            "SLIM2, 8577330, Solomons Island, MD ",
            "SLVM5, Silver Bay, MN ",
            "SNDA2, 9459450, Sand Point, AK ",
            "SNDP5, 1619910, Sand Island, Midway Islands ",
            "SPGF1, Settlement Point, GBI, Bahamas ",
            "SREF1, Shark River, FL ",
            "SRLM4, Spectacle Reef Light, MI ",
            "SRST2, Sabine Pass, TX ",
            "SSBN7, Sunset Beach Nearshore Waves ",
            "STDM4, Stannard Rock, MI ",
            "SVNM4, South Haven, MI ",
            "SWLA2, 9455090, Seward, AK ",
            "SWPM4, 9076070, S.W. Pier, MI ",
            "SWPV2, 8638610, Sewells Poif Pascagoula, MS ",
            "UNLA2, 9462620, Unalaska, AK ",
            "UPBC1, 9415118, Union Pacific Rail Road Bridge, Martinez, CA ",
            "VAKF1, 8723214, Virginia Key, FL ",
            "VCAF1, 8723970, Vaca Key, FL ",
            "VCAT2, 8773259, Port Lavaca, TX ",
            "VCVA2, 9464212, Village Cove, St. Paul Island, AK ",
            "VDZA2, 9454240, Valdez, AK ",
            "VENF1, Venice, FL ",
            "VERV4, Veracruz Harbor, MX ",
            "WAHV2, 8631044, Wachapreague, VA ",
            "WAKP8, 1890000, Wake Island ",
            "WASD2, 8594900, Washington, DC ",
            "WATS1, Lake Wateree, SC ",
            "WBYA1, 8732828, Weeks Bay, AL ",
            "WDSV2, 8638614, Willoughby Deguassing Station, VA ",
            "WDEL1, Shell West Delta 143 ",
            "WELM1, 8419317, Wells, ME ",
            "WEXM1, Laudholm Farm, Wells Reserve, ME ",
            "WHRI2, Waukegan Harbor, IL ",
            "WIWF1, Willy Willy, FL ",
            "WKXA1, Safe Harbor, Weeks Bay Reserve, AL ",
            "WLON7, 8658120, Wilmington, NC ",
            "WNEM4, 9076027, West Neebish Island, MI ",
            "WPLF1, Watson Place, FL ",
            "WPOW1, West Point, WA ",
            "WPTW1, 9441102, Westport, WA ",
            "WRBF1, Whipray Basin, FL ",
            "WSLM4, White Shoal Light, MI ",
            "WWEF1, White Water,West, FL ",
            "WYCM6, 8747437, Bay Waveland Yacht Club, MS ",
            "YABP4, 9754228, Yabucoa Harbor, PR ",
            "YATA2, 9453220, Yakutat, Yakutat Bay, AK ",
            "YGNN6, Niagara Coast Guard Station, NY ",
            "YKRV2, 8637611, York River East Rear Range Light, VA ",
            "YKTV2, 8637689, Yorktown, VA ",
            "YRSV2, Taskinas Creek, Chesapeake Bay, VA ",
    };

}
