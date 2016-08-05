package eliasdowling.com.buoy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import java.util.HashMap;
import java.util.Map;

//import eliasdowling.com.OpenBuoy.DataActivity;


public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.eliasdowling.buoy";
    private AutoCompleteTextView textView;
    private ListView lister;
    public static HashMap map;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //creating navigation drawer
        createNav();
        //creating search textView
        createAutoTextview();

        //sets favorites
        map = makeHash(FULLARRAY);
        //creates favorites
        getFav();
        favView(map);
    }

    @Override
    public void onResume(){
        super.onResume();
        favView(map);
        textView.setText("");
    }

    @Override
    public void onRestart() {
        super.onRestart();
        favView(map);
        textView.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);  // OPEN DRAWER
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createAutoTextview(){
        //Adapter to hold dropdown list
        FilterWithSpaceAdapter<String> adapter = new FilterWithSpaceAdapter<>(this,
                android.R.layout.simple_list_item_1, FULLARRAY);
        //main typable textview
        textView = (AutoCompleteTextView) findViewById(R.id.autoText);
        textView.setAdapter(adapter);

        //allows user to click on dropdown to take them directly to buoy page
        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(view, "Loading...", Snackbar.LENGTH_SHORT)
                        .show();
                sendBuoy(view);
            }
        });
    }

    private void createNav(){
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //navigation drawer stuff
        mDrawerList = (ListView)findViewById(R.id.navList);
        addDrawerItems();
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    SharedPreferences prefs = getSharedPreferences("Favorites",MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.clear().apply();
                }else if(position==1){
                    ndbc(view);
                }else if(position==2){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=eliasdowling.com.OpenBuoy")));
                }
            }
        });
    }

    private HashMap makeHash(String[] buoy){
        HashMap<String,String> map = new HashMap<>();
        for(int i=0;i<buoy.length;i++){
            map.put(buoy[i].substring(0,5),buoy[i]);
        }
        return map;
    }

    public void getMap(View view){
        Intent intent = new Intent(this,MapsActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the Enter button */
    public void sendBuoy(View view) {
        // Do something in response to button
        final Intent myIntent = new Intent(this,DataActivity.class);

        if(!textView.getText().toString().matches("")&&map.containsKey(textView.getText().toString().substring(0,5).toUpperCase())&&isNetworkAvailable(getApplicationContext())){
            myIntent.putExtra(EXTRA_MESSAGE, textView.getText().toString());
            startActivity(myIntent);
            onResume();
        }else if(isNetworkAvailable(getApplicationContext())){
            Snackbar.make(view, textView.getText().toString()+"Invalid buoy. Contact me if you want this buoy added!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else{
            Snackbar.make(view, "Connection unavailable", Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    private void favView(HashMap map){
        //holds favorites in array
        String[] fave = getFav();
        ExpandableRelativeLayout expandableLayout
                = (ExpandableRelativeLayout) findViewById(R.id.expandableLayout1);

        FilterWithSpaceAdapter<String> itemsAdapter =
                new FilterWithSpaceAdapter<>(this, R.layout.listthing,fave);

        lister = (ListView)findViewById(R.id.listy);

        lister.setAdapter(itemsAdapter);

        lister.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Snackbar.make(view, "Loading...", Snackbar.LENGTH_SHORT)
                        .show();

                // ListView Clicked item value
                String  itemValue    = (String) lister.getItemAtPosition(position);
                if(isNetworkAvailable(getApplicationContext())) {
                    Intent newIntent = new Intent(view.getContext(), DataActivity.class);
                    newIntent.putExtra(EXTRA_MESSAGE, itemValue);
                    startActivity(newIntent);
                }else{
                    Snackbar.make(view, "Connection unavailable", Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private String[] getFav(){
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
            Log.d("",key+val);
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
    private void ndbc(View v){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ndbc.noaa.gov/"));
        startActivity(browserIntent);
    }

    private void addDrawerItems() {
        String[] osArray = { "Clear all favorites", "All data from NDBC","Please rate my app!"};
        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
    }

    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

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
            "62029, K1 Buoy , UK",
            "62030, L4 Buoy , UK",
            "62050, E1 , UK",
            "62081, K2 Buoy , UK",
            "62095, M6, West Coast , UK",
            "62102, Armada AWS , UK",
            "62103, Channel Lightship , UK",
            "62104, Montrose , UK",
            "62105, K4 Buoy , UK",
            "62107, Sevenstones Lightship , UK",
            "62111, Goldeneye AWS , UK",
            "62112, Claymore AWS , UK",
            "62113, Piper , UK",
            "62114, Tartan A AWS , UK",
            "62115, Beatrice A , UK",
            "62116, Nelson AWS , UK",
            "62117, Buchan A , UK",
            "62118, Forties , UK",
            "62119, Shearwater AWS , UK",
            "62120, Fulmar , UK",
            "62121, Carrack AWS , UK",
            "62122, Etap AWS , UK",
            "62123, Janice A , UK",
            "62124, Conwy , UK",
            "62127, Cleeton AWS , UK",
            "62128, Miller AWS , UK",
            "62129, Saltire AWS , UK",
            "62130, Brae A , UK",
            "62131, Babbage AWS , UK",
            "62132, Auk , UK",
            "62133, Gannet AWS , UK",
            "62134, Andrew AWS , UK",
            "62135, Trent , UK",
            "62136, Katy , UK",
            "62137, Galaxy 1 , UK",
            "62138, Rough , UK",
            "62139, Loggs , UK",
            "62140, Transocean Rig 140 , UK",
            "62143, North Everest AWS , UK",
            "62144, Clipper AWS , UK",
            "62145, North Sea , UK",
            "62146, Lomond AWS , UK",
            "62148, Barque AWS , UK",
            "62149, West Sole A AWS , UK",
            "62150, Amethyst AWS , UK",
            "62151, Jade , UK",
            "62152, Elgin AWS , UK",
            "62153, Mungo AWS , UK",
            "62154, Clyde AWS , UK",
            "62155, Unity AWS , UK",
            "62157, Scott , UK",
            "62160, Judy , UK",
            "62161, Tiffany , UK",
            "62162, Kittiwake , UK",
            "62163, Brittany Buoy , UK",
            "62164, Anasuria AWS , UK",
            "62165, Ravenspurn North AWS , UK",
            "62167, Viking Bravo , UK",
            "62168, Britannia , UK",
            "62170, F3 Light Vessel , UK",
            "62296, Inde 23A , UK",
            "62297, East Brae , UK",
            "62302, Leman 27A , UK",
            "62304, Sandettie Lightship , UK",
            "62305, Greenwich Lightship , UK",
            "63055, Dunbar AWS , UK",
            "63056, Bruce AWS , UK",
            "63057, Harding AWS , UK",
            "63058, Clair Ridge , UK",
            "63059, Buzzard , UK",
            "63101, Tern , UK",
            "63102, Ninian Central , UK",
            "63103, North Cormorant AWS , UK",
            "63104, Dunlin , UK",
            "63105, Brent B AWS , UK",
            "63106, Brent , UK",
            "63107, Brent , UK",
            "63108, North Alwyn AWS , UK",
            "63109, UK , UK",
            "63110, Beryl A AWS , UK",
            "63111, Thistle Alpha , UK",
            "63112, Cormorant AWS , UK",
            "63113, Brent A AWS , UK",
            "63115, Magnus AWS , UK",
            "63117, Eider AWS , UK",
            "63118, Paul B. Lloyd Jr. , UK",
            "63120, Murdoch , UK",
            "64041, Clair AWS , UK",
            "64045, K5 Buoy , UK",
            "64046, K7 Buoy , UK",
            "64046, K7 Buoy , UK",
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
