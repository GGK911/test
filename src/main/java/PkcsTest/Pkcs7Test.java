package PkcsTest;

import lombok.SneakyThrows;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.security.Security;

/**
 * @author TangHaoKai
 * @version V1.0 2024/1/5 18:29
 **/
public class Pkcs7Test {

    @SneakyThrows
    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        String hex = "308212C2060A2A811CCF550601040202A08212B2308212AE020101310F300D06092A811CCF55018311020500300C060A2A811CCF550601040201A0820C3930820C3530820BDBA003020102020910528100000402937C300A06082A811CCF55018375308184310B300906035504061302434E310C300A06035504080C03202222310C300A06035504070C03202222310D300B060355040A0C04424A434131253023060355040B0C1C424A434120416E7977726974652054727573742053657276696365733123302106035504030C1A54727573742D5369676E20534D322043412D3220303030353030301E170D3232303531303037353033345A170D3232303531313037353033345A303C310B300906035504060C02434E312D302B06035504030C24E5B9B3E5AE89E7A791E68A80EFBC88E6B7B1E59CB3EFBC89E69C89E99990E585ACE58FB83059301306072A8648CE3D020106082A811CCF5501822D0342000491D28B11CCE7BD26834CE61A4821C0B0198F4E5CBB0E980463DB3BCFBB66CF5972B2546E821E8E8EB755B9D711E8285A2DE5F1857D2D4FE06959C72E8EF4A3FAA3820A7B30820A77301F0603551D23041830168014737A9DA707D21484D2BD6D7E00A894B385BAF423301D0603551D0E041604147E502EC72DD52F68A2CF1F937AB9C6B4CF1AB3EB300B0603551D0F0404030206C0308209E4060A2A811C86EF3202010901048209D40C8209D065794A575A584A7A61573975496A6F694D79347949697769535552556558426C496A6F694D534973496B6C45546E5674596D5679496A6F695A585631614768424B324E714C3363724E6B6F7859336C476232784A55315A525657387661444D795A47685052484930596A4E3061484678637A30694C434A535958644959584E6F496A6F69655564564D303078636A464E536D787861545A7256476C7A4E56526C656C493562546B304F55315852484A5A563149725356643151314247627A30694C434A43615739475A57463064584A6C496A7037496C4E6A636D6C776443493665794A4762334A74595851694F694A36615841694C434A586157523061434936496A45344D434973496B4E7662473979496A6F694D434973496B4E7664573530496A6F694D54453549697769556D566D56326C6B644767694F6A49774D437769556D566D534756705A326830496A6F784D444173496B5268644745694F694A6C536E683062465977567A564462306C6F54697436624852334E556C3152464176616D4D7953445A4B536D51344C335273656A5258526C6C525132396D613352305A446335616A6B76624556315257747A617A5654543074326433497A61325630637A6C77636E6F32646D4E775A6A64514E323078644731315432563555336878595339556555705157474533576D4A315132565055466C3552334D33556A4A7953444E6E6354597A616B35456379744B4E566B79596974754E57356861544A4C4D6B6C6B576C6F355A4577314D544E515A4556685A4731304D30786C6245703254326834536C4D33655841336553397961564E4C595373344E30316C4D69383161334A46626D6C49537A524459586852555739365A3246754F5864714B336877567A52355346647459326B336348525A515646715A30705461305A3254565255557A4E3162486436645652324B7A45324E3268334F466F3265465979626A4E355258707864307733547A4A364E6A4A6D59566C545A475A544E693946626B6F794B335A565A5656725347744E4D6C6C6A5955355951334177576D4647526E45324C323943636B707153584E6859574E4C616E4176546C707561474E6D5457785A5A326C53536B6C5155336C774D6A466D616D6C6E63454643556D31446557633262556C52516C426A6255564A65454E6C54575649563146495558553557584E71536E457A5A6A524C5A31645855324A495A6A52445A484D7A566A68566154567253444533596B31485A316B784E4764485333564F6332355559565A614E48553363305A314E5535435645353563307870527A51355A305A4F57473872646B744363564A545A564644576B745A656C4A57526A686F576A5A515456524F62473972534539695256464D556B6C35526D52314E48687451584E4364475A4B5230457765476479626C4E356332565551306B76535735495A7A6C475556525157555A4D556D6C30554570775957637A526E566951544E434E6C4E79576A644653464A32623039304D30685459305234574464494D6E4D774E6E4A4D63546B31646B4E70626B705657544673546D704C64556C6B52475A775455685356315A45646C5A4965545250525538796430646D4E334A56543235755A55686B63485259526E41315748524F5446565A616E56326269744851574A53596B3135626E524E576A4644516B31515A5646475A43394E5A446445536D5A496358565953473158546B5A344D6C566B52574679616E5A584D445977646E705262474A6D556E517A565564595955357A516A6C4253314645536A6443637A5270524756425A45316954584E3461485A526245315653556C5164444235625574524D4573335A46704253554676546A68455445565A5158466B5130567563557852626E7073597A5131543074714E566C7856465A514F55564457454E6163587046627A457856305236636D564C4F477473525545314E5746345658524764454E4463316C4564484E434D30644D6448564E5A6D703352485A596247685A6345315562575534526C553361336C3462323835537A426C546E6C31516D64324D306458616C56546445644B644464575355564665437377576B5578536E466C566C6C53516E6F3064336C4E536D39545745395959315A7562793935536E4E4A5A44644C516E704C513056354D33684A63305A6F5A6D6772516E425664316F7A4D307430524339615448643464325A535130396A646C564C623056355555557A6554645A6346684361574A7A596A685964326471546C70464F556C33513164754F4846524D6C4A72656C5534546C4245544739496156637853474E505A6D67344D555A335A576C3652585A5A55466434636C4E4356476B7651307071543163786147786D636C42324E48466F5258706E4D6D64715954513461545643543268304F437449553252775347466F535852514E45747A4B324930526C6C6B5A307854543370705346686862477855536C7030546A684A6253394D4E473834553259344D5463344B326431646A4531655567694C434A455A585A70593255694F6E73695247563261574E6C546D46745A534936496B464F52464A5053555266554546455830356C64484E6A5958426C49697769553246746347786C556D46305A534936496A45774D6A51694C434A51636D567A6330316865434936496A45774D6A51694C434A5861575230614349364F546B354F546B73496B686C6157646F644349364F546B354F546B73496B527961585A6C636C5A6C63694936496E59784C6A416966583073496C426F6233527651584A7959586B694F6C74644C434A54623356755A454679636D4635496A7062585377695647563464454679636D4635496A706265794A44623235305A573530496A6F696531776964584E6C636C77694F6C77693570326F3562694758434A39496E31644C434A506447686C636B4679636D4635496A706258583073496B4E736157567564453954496A7037496B3568625755694F694A585A576C59615735666156426F6232356C496977695257527064476C7662694936496B3176656D6C73624745694C434A545A584A3261574E6C5547466A61794936496B3576626D55694C434A575A584A7A61573975496A6F694E5334774943687055476876626D553749454E515653427055476876626D556754314D674D5452664E6942736157746C4945316859794250557942594B534242634842735A56646C596B7470644338324D4455754D5334784E53416F533068555455777349477870613255675232566A6132387049453176596D6C735A5338784E5555784E44676749485A6C636E4E70623235446232526C505459324D54417749476C51614739755A565235634755396156426F6232356C4D544D734D69426A6248567A644756794C575675646A30675545465164576831615546776344314F5A5864516457683161534973496B395451584A6A61434936496A4D794C7A5930496E313930400603551D2004393037303506092A811C86EF320202023028302606082B06010505070201161A687474703A2F2F7777772E626A63612E6F72672E636E2F637073300A06082A811CCF550183750348003045022100C7B21733049C05DF60C61A6722973AC7614930597E8B92BA0FB6C73367944F7C022061C065FA99756D680F6B32E2E10733657E942811D088E0447CE63C2CF2335BBB3182064B30820647020101308192308184310B300906035504061302434E310C300A06035504080C03202222310C300A06035504070C03202222310D300B060355040A0C04424A434131253023060355040B0C1C424A434120416E7977726974652054727573742053657276696365733123302106035504030C1A54727573742D5369676E20534D322043412D3220303030353030020910528100000402937C300D06092A811CCF55018311020500A06A301906092A864886F70D010903310C060A2A811CCF550601040201301C06092A864886F70D010905310F170D3232303531303037353033345A302F06092A864886F70D010904312204209A036CA8F39A8E1CE8B404BC1EF194443E2C198D2A9630D90E1AF789094F61D3300D06092A811CCF5501822D01050004483046022100F6FA67D8DD56C98FAF143FDDBBA60FCC8D6D01653F29D9F29ACDB1DA05EE84F1022100D11DD81DAC822614E1E117937A95935B94ED0C61E0595540D1748AF3D7427FD9A18204D7308204D3060B2A864886F70D010910020E318204C2308204BE060A2A811CCF550601040202A08204AE308204AA020101310F300D06092A811CCF550183110105003066060B2A864886F70D0109100104A0570455305302010106082B06010505070308302D06092A811CCF55018311010420EA89FB33C40D1AAB269EA7BC9AF8CCABAF10A05016A074DF7FD0D106EFA45E64020405F5E101180F32303232303531303037353033345AA08202F7308202F33082029AA003020102020A1A1000000000002ADC0B300A06082A811CCF550183753044310B300906035504061302434E310D300B060355040A0C04424A4341310D300B060355040B0C04424A43413117301506035504030C0E4265696A696E6720534D32204341301E170D3135313130353136303030305A170D3235313130363135353935395A3081A3311E301C06035504030C15E697B6E997B4E688B3E69C8DE58AA1E8AF81E4B9A631543052060355040B0C4BE5B9B3E5AE89E7A791E68A80E7A7BBE58AA8E794B5E5AD90E4BF9DE58D95E7B3BBE7BB9FE5AE9AE588B6E58C96E6898BE58699E695B0E5AD97E7ADBEE5908DE5BA94E794A8E9A1B9E79BAE310D300B060355040A0C04424A4341310F300D060355040A0C06424A43415453310B300906035504060C02434E3059301306072A8648CE3D020106082A811CCF5501822D034200046D88986370960179D142C6D27D6F4CD59F695BC7D3F8342D04A6E4E80A1D490851EC9135586A4FD129D5387A271C97B79EEF00A2AD433F9B21C4F786BF4F7DE5A38201123082010E301F0603551D230418301680141FE6CFD48FC5222A974A298A15E716C99234C4B630819D0603551D1F0481953081923060A05EA05CA45A3058310B300906035504061302434E310D300B060355040A0C04424A4341310D300B060355040B0C04424A43413117301506035504030C0E4265696A696E6720534D3220434131123010060355040313096361323163726C3135302EA02CA02A8628687474703A2F2F63726C2E626A63612E6F72672E636E2F63726C2F6361323163726C31352E63726C301106096086480186F84201010404030200FF300B0603551D0F0404030203F830160603551D250101FF040C300A06082B060105050703083013060A2A811C86EF320201011E04050C03363534300A06082A811CCF5501837503470030440220678A0042D66802375ECAF9FB4F05580FC5926D95E859873C82DF0151CB6F9A1102203BCB6DDA84A969ADF976A1F164BEC0B37380C467507AD10F7AEF450E97A409D23182012F3082012B02010130523044310B300906035504061302434E310D300B060355040A0C04424A4341310D300B060355040B0C04424A43413117301506035504030C0E4265696A696E6720534D32204341020A1A1000000000002ADC0B300C06082A811CCF550183110500A06B301A06092A864886F70D010903310D060B2A864886F70D0109100104301C06092A864886F70D010905310F170D3232303531303037353033345A302F06092A864886F70D0109043122042003FDE0D0E3CBD69005F477DD335A1EEA94B26F5ED8DB1A69C0195679D51F8007300D06092A811CCF5501822D01050004483046022100AEAE3D38535E9188F2DDC3BA81784AAC09953035EB34E1CE5EEF8739F7723BF50221008C4C6433354D18DD523883859C8897BB2A08EB48D07A5310FE7EF07F27390578";
        ASN1InputStream asn1InputStream = new ASN1InputStream(Hex.decode(hex));
        //将hex转换为byte输出
        ASN1Primitive asn1Primitive;
        while ((asn1Primitive = asn1InputStream.readObject()) != null) {
            //循环读取，分类解析。这样的解析方式可能不适合有两个同类的ASN1对象解析，如果遇到同类，那就需要按照顺序来调用readObject，就可以实现解析了。
            ASN1Sequence sequence = (ASN1Sequence) asn1Primitive;
            System.out.println(sequence.size());
            ASN1TaggedObject taggedObject = (ASN1TaggedObject) sequence.getObjectAt(1);
            ASN1Sequence sequence1 = (ASN1Sequence) taggedObject.getBaseObject();
            ASN1TaggedObject taggedObject1 = (ASN1TaggedObject) sequence1.getObjectAt(3);
            ASN1Sequence sequence2 = (ASN1Sequence) taggedObject1.getBaseObject();
            System.out.println(Base64.toBase64String(sequence2.getEncoded()));

        }
    }
}
