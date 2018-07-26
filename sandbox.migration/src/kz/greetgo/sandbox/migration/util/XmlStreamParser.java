package kz.greetgo.sandbox.migration.util;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.*;


import kz.greetgo.sandbox.controller.model.tmpmodels.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class XmlStreamParser
//        extends SaxHandler
{


//    @Override
//    protected void startingTag(Attributes attributes) {
//        String path = path();
//        if ("/record".equals(path)) {
//            id = attributes.getValue("id");
//            return;
//        }
//        if ("/record/names".equals(path)) {
//            surname = attributes.getValue("surname");
//            name = attributes.getValue("name");
//            patronymic = attributes.getValue("patronymic");
//
//            return;
//        }
//    }
//
//    @Override
//    protected void endedTag(String tagName) throws Exception {
//
//        String path = path() + "/" + tagName;
//
//        if ("/record/birth_date".equals(path)) {
//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//            birthDate = new java.sql.Date(sdf.parse(text()).getTime());
//            return;
//        }
//    }




    public static String createBundle(BufferedReader br) throws IOException {

        StringBuilder xml = new StringBuilder();
        String st;
//        StringBuilder opening = new StringBuilder();

        StringBuilder name = new StringBuilder();
        StringBuilder surname = new StringBuilder();
        StringBuilder patronymic = new StringBuilder();
        StringBuilder gender = new StringBuilder();
        StringBuilder birthDate = new StringBuilder();
        StringBuilder homePhones = new StringBuilder();
        StringBuilder workPhones = new StringBuilder();
        StringBuilder mobilePhones = new StringBuilder();
        StringBuilder embeddedPhones = new StringBuilder();
        StringBuilder addressOpenTag = new StringBuilder();
        StringBuilder facAddress = new StringBuilder();
        StringBuilder regAddress = new StringBuilder();
        StringBuilder addressCloseTag = new StringBuilder();

        boolean started = false;
        while((st=br.readLine())!=null){

                if(st.matches("(\\s*)<\\/cia>(\\s*)")) {
                        started = false;
                        return null;
                }
            if(st.matches("(\\s*)<\\/client>(\\s*)")){
                //"<\\/client>"
                xml.append(name).
                    append(patronymic).
                    append(surname).
                    append(gender).
                    append(birthDate).
                    append(homePhones).
                    append(workPhones).
                    append(mobilePhones).
                    append(embeddedPhones).
                    append(addressOpenTag).
                    append(facAddress).
                    append(regAddress).
                    append(addressCloseTag).
                    append(st);

                started=false;
                break;
            }
            if(started){
                if(st.matches("(\\s*)<name value=\".+\"/>(\\s*)")){
                    name.append(st);

                }
                if(st.matches("(\\s*)<patronymic value=\".+\"/>(\\s*)")){
                    patronymic.append(st);
                }
                if(st.matches("(\\s*)<surname value=\".+\"/>(\\s*)")){
                    surname.append(st);
                }
                if(st.matches("(\\s*)<gender value=\".+\"/>(\\s*)")){
                    gender.append(st);
                }
                if(st.matches("(\\s*)<birth value=\".+\"/>(\\s*)")){
                    birthDate.append(st);
                }
                if(st.matches("(\\s*)<homePhone>.+</homePhone>(\\s*)")){
                    homePhones.append(st);
                }
                if(st.matches("(\\s*)<workPhone>.+</workPhone>(\\s*)")){
                    workPhones.append(st);
                }
                if(st.matches("(\\s*)<mobilePhone>.+</mobilePhone>(\\s*)")){
                    mobilePhones.append(st);
                }
                if(st.matches("(\\s*)<embeddedPhone>.+</embeddedPhone>(\\s*)")){
                    embeddedPhones.append(st);
                }


                if(st.matches("(\\s*)<address>(\\s*)")){
                    addressOpenTag.append(st);
                }
                if(st.matches("(\\s*)<factual.+/>(\\s*)")){
                    facAddress.append(st);
                }
                if(st.matches("(\\s*)<register.+/>(\\s*)")){
                    regAddress.append(st);
                }
                if(st.matches("(\\s*)</address>(\\s*)")){
                    addressCloseTag.append(st);
                }
            }
            if(st.matches("(\\s*)<client id=\".+\"> <!--.+-->(\\s*)")){
                xml.append(st);
                started = true;
            }
        }
        return xml.toString();
    }

    static public void createTmpClient(String xmlBundle){
        TmpClientDetails tmpClientDetails = new TmpClientDetails();
        ArrayList<TmpPhone> tmpPhones = new ArrayList<>();


        int nextIndex = 0;

        Pattern clientId = Pattern.compile("<client id=\"(.*?)\">");
        Matcher clientIdMatcher = clientId.matcher(xmlBundle);
        while(clientIdMatcher.find()){
            tmpClientDetails.id=clientIdMatcher.group(1);
            clientIdMatcher.group(0);
            nextIndex = clientIdMatcher.end();
        }

        Pattern ciaId = Pattern.compile("\\<!--(.*?)\\-->");
        Matcher ciaIdMatcher = ciaId.matcher(xmlBundle);
        while(ciaIdMatcher.find(nextIndex)){
            tmpClientDetails.ciaId=ciaIdMatcher.group(1);
            ciaIdMatcher.group(0);
            nextIndex = ciaIdMatcher.end();
        }

        Pattern name = Pattern.compile("<name value=\"(.*?)\"/>");
        Matcher nameMatcher = name.matcher(xmlBundle);
        while(nameMatcher.find(nextIndex)){
            tmpClientDetails.name=nameMatcher.group(1);
            nameMatcher.group(0);
            nextIndex = nameMatcher.end();
        }

        Pattern patronymic = Pattern.compile("<patronymic value=\"(.*?)\"/>");
        Matcher patronymicMatcher = patronymic.matcher(xmlBundle);
        while(patronymicMatcher.find(nextIndex)){
            tmpClientDetails.patronymic=patronymicMatcher.group(1);
            patronymicMatcher.group(0);
            nextIndex = patronymicMatcher.end();
        }

//        Pattern gender = Pattern.compile("<gender value=\"(.*?)\"/>");


        Pattern surname = Pattern.compile("<surname value=\"(.*?)\"/>");
        Matcher surnameMatcher = surname.matcher(xmlBundle);
        while(surnameMatcher.find(nextIndex)){
            tmpClientDetails.surname=surnameMatcher.group(1);
            surnameMatcher.group(0);
            nextIndex = surnameMatcher.end();
        }


        Pattern birthDate = Pattern.compile("<birth value=\"(.*?)\"/>");
        Matcher birthDateMatcher = birthDate.matcher(xmlBundle);
        while(birthDateMatcher.find(nextIndex)){
            tmpClientDetails.birthDate=birthDateMatcher.group(1);
            birthDateMatcher.group(0);
            nextIndex = birthDateMatcher.end();
        }



        Pattern homePhone = Pattern.compile("<homePhone>(.*?)</homePhone>");
        Matcher homePhoneMatcher = homePhone.matcher(xmlBundle);
        while(homePhoneMatcher.find(nextIndex)){
            TmpPhone tmpPhone = new TmpPhone();
            tmpPhone.phoneType = "HOME";
            tmpPhone.number = homePhoneMatcher.group(1);
            tmpPhones.add(tmpPhone);
            homePhoneMatcher.group(0);
            nextIndex = homePhoneMatcher.end();
        }

        Pattern workPhone = Pattern.compile("<workPhone>(.*?)</workPhone>");
        Matcher workPhoneMatcher = workPhone.matcher(xmlBundle);
        while(homePhoneMatcher.find(nextIndex)){
            TmpPhone tmpPhone = new TmpPhone();
            tmpPhone.phoneType = "WORK";
            tmpPhone.number = workPhoneMatcher.group(1);
            tmpPhones.add(tmpPhone);
            workPhoneMatcher.group(0);
            nextIndex = workPhoneMatcher.end();
        }

        Pattern mobilePhone = Pattern.compile("<mobilePhone>(.*?)</mobilePhone>");
        Matcher mobilePhoneMatcher = mobilePhone.matcher(xmlBundle);
        while(mobilePhoneMatcher.find(nextIndex)){
            TmpPhone tmpPhone = new TmpPhone();
            tmpPhone.phoneType = "MOBILE";
            tmpPhone.number = mobilePhoneMatcher.group(1);
            tmpPhones.add(tmpPhone);
            mobilePhoneMatcher.group(0);
            nextIndex = mobilePhoneMatcher.end();
        }

        Pattern embeddedPhone = Pattern.compile("<embeddedPhone>(.*?)</embeddedPhone>");
        Matcher embeddedPhoneMatcher = embeddedPhone.matcher(xmlBundle);
        while(embeddedPhoneMatcher.find(nextIndex)){
            TmpPhone tmpPhone = new TmpPhone();
            tmpPhone.phoneType = "EMBEDDED";
            tmpPhone.number = embeddedPhoneMatcher.group(1);
            tmpPhones.add(tmpPhone);
            embeddedPhoneMatcher.group(0);
            nextIndex = embeddedPhoneMatcher.end();
        }

        tmpClientDetails.tmpPhones = tmpPhones;
        //<fact street="SР№Р±ByРЃvEРЃzРѕnYРјuGIСЋСЂРҐ" house="Ph" flat="РѕС‹"/>


        Pattern facAddr = Pattern.compile("<fact\"(.*?)\"/>");
        Matcher facAddrMatcher = facAddr.matcher(xmlBundle);
        while (facAddrMatcher.find(nextIndex)){
            String innerContent = facAddrMatcher.group(1);
            tmpClientDetails.tmpFacAddress = (TmpFacAddress) tmpAddressFromString(innerContent);
            facAddrMatcher.group(0);
            nextIndex = facAddrMatcher.end();
        }

        Pattern regAddr = Pattern.compile("<register\"(.*?)\"/>");
        Matcher regAddrMatcher = regAddr.matcher(xmlBundle);
        while (regAddrMatcher.find(nextIndex)){
            String innerContent = regAddrMatcher.group(1);
            tmpClientDetails.tmpRegAddress = (TmpRegAddress) tmpAddressFromString(innerContent);
            regAddrMatcher.group(0);
            nextIndex = regAddrMatcher.end();
        }

        System.out.println(tmpClientDetails);
    }

    static public TmpAddress tmpAddressFromString(String innerContent){

        Pattern street = Pattern.compile("street\"(.*?)\"");
        Pattern house = Pattern.compile("house\"(.*?)\"");
        Pattern flat = Pattern.compile("flat\"(.*?)\"");

        TmpAddress  tmpAddress = new TmpAddress();
        int nextInnerIndex = 0;
        Matcher streetMatcher = street.matcher(innerContent);
        while (streetMatcher.find(nextInnerIndex)){
            tmpAddress.street = streetMatcher.group(1);
            streetMatcher.group(0);
            nextInnerIndex = streetMatcher.end();
        }
        Matcher houseMatcher = house.matcher(innerContent);
        while (houseMatcher.find(nextInnerIndex)){
            tmpAddress.house = houseMatcher.group(1);
            houseMatcher.group(0);
            nextInnerIndex = houseMatcher.end();
        }

        Matcher flatMatcher = flat.matcher(innerContent);
        while (flatMatcher.find(nextInnerIndex)){
            tmpAddress.house = flatMatcher.group(1);
            flatMatcher.group(0);
            nextInnerIndex = flatMatcher.end();
        }
        return tmpAddress;
    }

    static public void main(String[] args) throws IOException {
//        String in = "<cia>\n" +
//                "  <client id=\"0-B9N-HT-PU-04wolRBPzj\"> <!-- 1 -->\n" +
//                "    <surname value=\"Р“RРЅРЁР±7gDn1\"/>\n" +
//                "    <workPhone>+7-385-253-53-56</workPhone>\n" +
//                "    <address>\n" +
//                "      <fact street=\"RС†Р’WAР°EkMРєС‘РЅkOР·Р”fР¶Р“Рє\" house=\"RРџ\" flat=\"hР\u0098\"/>\n" +
//                "      <register street=\"РҐfРђР\u0098KР»FС‰СЃiС…Р”Р—СЂPРіWР—dР\u00AD\" house=\"Рѕz\" flat=\"Р Р‘\"/>\n" +
//                "    </address>\n" +
//                "    <birth value=\"1995-07-07\"/>\n" +
//                "    <patronymic value=\"  \"/>\n" +
//                "    <charm value=\"Р©lР’OpР¤pРЄР\u0098РЁ\"/>\n" +
//                "    <gender value=\"FEMALE\"/>\n" +
//                "    <homePhone>+7-878-241-63-94</homePhone>\n" +
//                "    <name value=\"WCР\u0098TР‘РЇ7С‰РђРѕ\"/>\n" +
//                "    <workPhone>+7-418-204-55-17</workPhone>\n" +
//                "    <workPhone>+7-922-435-88-49</workPhone>\n" +
//                "  </client>\n" +
//                "  <client id=\"0-B9N-HT-PU-04wolRBPzj\"> <!-- 2 -->\n" +
//                "    <name value=\"NIfРўDС‚СѓРЇkРў\"/>\n" +
//                "    <charm value=\"Р‘РћРЁРҐС‹5UРЄРњР—\"/>\n" +
//                "    <patronymic value=\"РЈР”С‚Р®С†Рљp5РЃР›Р°Р№l\"/>\n" +
//                "    <homePhone>+7-568-155-69-48 РІРЅ. 8929</homePhone>\n" +
//                "    <mobilePhone>+7-345-524-77-87 РІРЅ. 8884</mobilePhone>\n" +
//                "    <gender value=\"MALE\"/>\n" +
//                "    <address>\n" +
//                "      <fact street=\"SР№Р±ByРЃvEРЃzРѕnYРјuGIСЋСЂРҐ\" house=\"Ph\" flat=\"РѕС‹\"/>\n" +
//                "      <register street=\"wРІlogР‘gР§Р©fР 4zkСЃRbDР¶Р¶\" house=\"SС†\" flat=\"Р©Рµ\"/>\n" +
//                "    </address>\n" +
//                "    <birth value=\"1966-03-10\"/>\n" +
//                "    <workPhone>+7-663-920-15-23 РІРЅ. 3649</workPhone>\n" +
//                "    <surname value=\"Р•DС„Р·EСѓР§СЊ57\"/>\n" +
//                "  </client>\n" +
//                "  <client id=\"2-MFR-HN-2X-LPT6Ex2SPW\"> <!-- 3 -->\n" +
//                "    <workPhone>+7-982-113-87-52</workPhone>\n" +
//                "    <gender value=\"MALE\"/>\n" +
//                "    <mobilePhone>+7-219-877-56-03</mobilePhone>\n" +
//                "    <mobilePhone>+7-565-258-17-28</mobilePhone>\n" +
//                "    <patronymic value=\"РЃWР›2СЏ1РљР¬РћLAР¤СЉ\"/>\n" +
//                "    <name value=\"2РІС†РЅwРєРџnРЈРё\"/>\n" +
//                "    <birth value=\"1941-07-23\"/>\n" +
//                "    <address>\n" +
//                "      <fact street=\"Z6lС‚C8lС†Р”РєIYv6Р\u00ADРњXpxР°\" house=\"Рљb\" flat=\"Р\u0098D\"/>\n" +
//                "      <register street=\"Р°РґvРћРёjРЎjCС‹NР¶СЉР›С‚hРїР§С„Рі\" house=\"С‡W\" flat=\"Рїw\"/>\n" +
//                "    </address>\n" +
//                "    <workPhone>+7-219-164-33-51</workPhone>\n" +
//                "    <surname value=\"Р¤AcgsHZРЄРєp\"/>\n" +
//                "    <workPhone>+7-359-831-31-82</workPhone>\n" +
//                "    <charm value=\"РїlJРҐС‘sСЉРЎAРЁ\"/>\n" +
//                "    <mobilePhone>+7-286-148-26-75 РІРЅ. 3136</mobilePhone>\n" +
//                "  </client>\n" +
//                " </cia>";
//
//        Pattern p = Pattern.compile("\\<!-asdsad-(.*?)\\-->");
//        Matcher m = p.matcher(in);
//
//
//        while(m.find()) {
//            System.out.println(m.group(1));
//            System.out.println(m.group());
//            System.out.println(m.end());
//        }


        File file = new File("D:\\greetgonstuff\\greetgo.sandbox\\cia.txt");
        FileInputStream fis = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        int a = 0;
        String bundle;
        while((bundle = createBundle(br)
            )!=null){
            createTmpClient(bundle);
            System.out.println(a+"\n\n");
            a++;
        }
        br.close();
        fis.close();



    }

//    TmpClientDetails tmpCD = new TmpClientDetails();
//    boolean bId = false;
//
//    boolean bName = false;
//    boolean bSurname = false;
//    boolean bPatronymic = false;
//    boolean bBirthDate = false;
//    boolean bAddress = false;
//    boolean bRegAddress = false;
//    boolean bPhone = false;
//    boolean bMobilePhone = false;


//    tmpCD.id = "1";

//    TmpRegAddress regAddress = new TmpRegAddress();
//    regAddress =

//    @Override
//    public void startElement(String uri, String localName, String qName, Attributes attributes)
//            throws SAXException {
//
//        if (qName.equalsIgnoreCase("client")) {
//            //create a new Employee and put it in Map
//            String id = attributes.getValue("id");
//            //initialize Employee object and set id attribute
//            TmpClientDetails tmpCd = new TmpClientDetails();
//            tmpCd.id = id;
//            //initialize list
//            if (empList == null) empList = new ArrayList<>();
//        } else if (qName.equalsIgnoreCase("birth")) {
//            bName = true;
//        } else if (qName.equalsIgnoreCase("age")) {
//            bAge = true;
//        } else if (qName.equalsIgnoreCase("gender")) {
//            bGender = true;
//        } else if (qName.equalsIgnoreCase("role")) {
//            bRole = true;
//        }
//    }
//
//    @Override
//    public void endElement(String uri, String localName, String qName) throws SAXException {
//        if (qName.equalsIgnoreCase("Employee")) {
//            //add Employee object to list
//            empList.add(emp);
//        }
//    }
//
//    @Override
//    public void characters(char ch[], int start, int length) throws SAXException {
//
//        if (bAge) {
//            //age element, set Employee age
//            emp.setAge(Integer.parseInt(new String(ch, start, length)));
//            bAge = false;
//        } else if (bName) {
//            emp.setName(new String(ch, start, length));
//            bName = false;
//        } else if (bRole) {
//            emp.setRole(new String(ch, start, length));
//            bRole = false;
//        } else if (bGender) {
//            emp.setGender(new String(ch, start, length));
//            bGender = false;
//        }
//    }
//
//
}
