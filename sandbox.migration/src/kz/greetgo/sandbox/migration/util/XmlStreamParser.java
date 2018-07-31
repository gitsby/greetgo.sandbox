//package kz.greetgo.sandbox.migration.util;
//
//
//
//
//
//import kz.greetgo.sandbox.controller.model.tmpmodels.*;
//
//import java.io.*;
//
//import java.util.ArrayList;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//
//public class XmlStreamParser{
//
//
//
//
//    public static String createBundle(BufferedReader br) throws IOException {
//
//        StringBuilder xml = new StringBuilder();
//        String st;
////        StringBuilder opening = new StringBuilder();
//
//        StringBuilder name = new StringBuilder();
//        StringBuilder surname = new StringBuilder();
//        StringBuilder patronymic = new StringBuilder();
//        StringBuilder gender = new StringBuilder();
//        StringBuilder birthDate = new StringBuilder();
//        StringBuilder homePhones = new StringBuilder();
//        StringBuilder workPhones = new StringBuilder();
//        StringBuilder mobilePhones = new StringBuilder();
//        StringBuilder embeddedPhones = new StringBuilder();
//        StringBuilder addressOpenTag = new StringBuilder();
//        StringBuilder facAddress = new StringBuilder();
//        StringBuilder regAddress = new StringBuilder();
//        StringBuilder addressCloseTag = new StringBuilder();
//
//        boolean started = false;
//        while((st=br.readLine())!=null){
//
//                if(st.matches("(\\s*)<\\/cia>(\\s*)")) {
//                        started = false;
//                        return null;
//                }
//            if(st.matches("(\\s*)<\\/client>(\\s*)")){
//                //"<\\/client>"
//                xml.append(name).
//                    append(patronymic).
//                    append(surname).
//                    append(gender).
//                    append(birthDate).
//                    append(homePhones).
//                    append(workPhones).
//                    append(mobilePhones).
//                    append(embeddedPhones).
//                    append(addressOpenTag).
//                    append(facAddress).
//                    append(regAddress).
//                    append(addressCloseTag).
//                    append(st);
//
//                started=false;
//                break;
//            }
//            if(started){
//                if(st.matches("(\\s*)<name value=\".+\"/>(\\s*)")){
//                    name.append(st);
//
//                }
//                if(st.matches("(\\s*)<patronymic value=\".+\"/>(\\s*)")){
//                    patronymic.append(st);
//                }
//                if(st.matches("(\\s*)<surname value=\".+\"/>(\\s*)")){
//                    surname.append(st);
//                }
//                if(st.matches("(\\s*)<gender value=\".+\"/>(\\s*)")){
//                    gender.append(st);
//                }
//                if(st.matches("(\\s*)<birth value=\".+\"/>(\\s*)")){
//                    birthDate.append(st);
//                }
//                if(st.matches("(\\s*)<homePhone>.+</homePhone>(\\s*)")){
//                    homePhones.append(st);
//                }
//                if(st.matches("(\\s*)<workPhone>.+</workPhone>(\\s*)")){
//                    workPhones.append(st);
//                }
//                if(st.matches("(\\s*)<mobilePhone>.+</mobilePhone>(\\s*)")){
//                    mobilePhones.append(st);
//                }
//                if(st.matches("(\\s*)<embeddedPhone>.+</embeddedPhone>(\\s*)")){
//                    embeddedPhones.append(st);
//                }
//
//
//                if(st.matches("(\\s*)<address>(\\s*)")){
//                    addressOpenTag.append(st);
//                }
//                if(st.matches("(\\s*)<fact.+/>(\\s*)")){
//                    facAddress.append(st);
//                }
//                if(st.matches("(\\s*)<register.+/>(\\s*)")){
//                    regAddress.append(st);
//                }
//                if(st.matches("(\\s*)</address>(\\s*)")){
//                    addressCloseTag.append(st);
//                }
//            }
//            if(st.matches("(\\s*)<client id=\".+\"> <!--.+-->(\\s*)")){
//                xml.append(st);
//                started = true;
//            }
//        }
//        return xml.toString();
//    }
//
//    static public void createTmpClient(String xmlBundle){
//        TmpClientDetails tmpClientDetails = new TmpClientDetails();
//        ArrayList<TmpPhone> tmpPhones = new ArrayList<>();
//
//
//        int nextIndex = 0;
//
//        Pattern clientId = Pattern.compile("<client id=\"(.*?)\">");
//        Matcher clientIdMatcher = clientId.matcher(xmlBundle);
//        while(clientIdMatcher.find()){
//            tmpClientDetails.id=clientIdMatcher.group(1);
//            clientIdMatcher.group(0);
//            nextIndex = clientIdMatcher.end();
//        }
//
//        Pattern ciaId = Pattern.compile("\\<!--(.*?)\\-->");
//        Matcher ciaIdMatcher = ciaId.matcher(xmlBundle);
//        while(ciaIdMatcher.find(nextIndex)){
//            tmpClientDetails.ciaId=ciaIdMatcher.group(1);
//            ciaIdMatcher.group(0);
//            nextIndex = ciaIdMatcher.end();
//        }
//
//        Pattern name = Pattern.compile("<name value=\"(.*?)\"/>");
//        Matcher nameMatcher = name.matcher(xmlBundle);
//        while(nameMatcher.find(nextIndex)){
//            tmpClientDetails.name=nameMatcher.group(1);
//            nameMatcher.group(0);
//            nextIndex = nameMatcher.end();
//        }
//
//        Pattern patronymic = Pattern.compile("<patronymic value=\"(.*?)\"/>");
//        Matcher patronymicMatcher = patronymic.matcher(xmlBundle);
//        while(patronymicMatcher.find(nextIndex)){
//            tmpClientDetails.patronymic=patronymicMatcher.group(1);
//            patronymicMatcher.group(0);
//            nextIndex = patronymicMatcher.end();
//        }
//
//
//        Pattern surname = Pattern.compile("<surname value=\"(.*?)\"/>");
//        Matcher surnameMatcher = surname.matcher(xmlBundle);
//        while(surnameMatcher.find(nextIndex)){
//            tmpClientDetails.surname=surnameMatcher.group(1);
//            surnameMatcher.group(0);
//            nextIndex = surnameMatcher.end();
//        }
//
//
//        Pattern gender = Pattern.compile("<gender value=\"(.*?)\"/>");
//        Matcher genderMatcher = gender.matcher(xmlBundle);
//        while(genderMatcher.find(nextIndex)){
//            tmpClientDetails.gender =genderMatcher.group(1);
//            genderMatcher.group(0);
//            nextIndex = genderMatcher.end();
//        }
//
//
//        Pattern birthDate = Pattern.compile("<birth value=\"(.*?)\"/>");
//        Matcher birthDateMatcher = birthDate.matcher(xmlBundle);
//        while(birthDateMatcher.find(nextIndex)){
//            tmpClientDetails.birthDate=birthDateMatcher.group(1);
//            birthDateMatcher.group(0);
//            nextIndex = birthDateMatcher.end();
//        }
//
//
//
//        Pattern homePhone = Pattern.compile("<homePhone>(.*?)</homePhone>");
//        Matcher homePhoneMatcher = homePhone.matcher(xmlBundle);
//        while(homePhoneMatcher.find(nextIndex)){
//            TmpPhone tmpPhone = new TmpPhone();
//            tmpPhone.phoneType = "HOME";
//            tmpPhone.number = homePhoneMatcher.group(1);
//            tmpPhones.add(tmpPhone);
//            homePhoneMatcher.group(0);
//            nextIndex = homePhoneMatcher.end();
//        }
//
//        Pattern workPhone = Pattern.compile("<workPhone>(.*?)</workPhone>");
//        Matcher workPhoneMatcher = workPhone.matcher(xmlBundle);
//        while(homePhoneMatcher.find(nextIndex)){
//            TmpPhone tmpPhone = new TmpPhone();
//            tmpPhone.phoneType = "WORK";
//            tmpPhone.number = workPhoneMatcher.group(1);
//            tmpPhones.add(tmpPhone);
//            workPhoneMatcher.group(0);
//            nextIndex = workPhoneMatcher.end();
//        }
//
//        Pattern mobilePhone = Pattern.compile("<mobilePhone>(.*?)</mobilePhone>");
//        Matcher mobilePhoneMatcher = mobilePhone.matcher(xmlBundle);
//        while(mobilePhoneMatcher.find(nextIndex)){
//            TmpPhone tmpPhone = new TmpPhone();
//            tmpPhone.phoneType = "MOBILE";
//            tmpPhone.number = mobilePhoneMatcher.group(1);
//            tmpPhones.add(tmpPhone);
//            mobilePhoneMatcher.group(0);
//            nextIndex = mobilePhoneMatcher.end();
//        }
//
//        Pattern embeddedPhone = Pattern.compile("<embeddedPhone>(.*?)</embeddedPhone>");
//        Matcher embeddedPhoneMatcher = embeddedPhone.matcher(xmlBundle);
//        while(embeddedPhoneMatcher.find(nextIndex)){
//            TmpPhone tmpPhone = new TmpPhone();
//            tmpPhone.phoneType = "EMBEDDED";
//            tmpPhone.number = embeddedPhoneMatcher.group(1);
//            tmpPhones.add(tmpPhone);
//            embeddedPhoneMatcher.group(0);
//            nextIndex = embeddedPhoneMatcher.end();
//        }
//
//        tmpClientDetails.tmpPhones = tmpPhones;
//
//
//        Pattern facAddr = Pattern.compile("<fact(.*?)/>");
//        Matcher facAddrMatcher = facAddr.matcher(xmlBundle);
//        while (facAddrMatcher.find(nextIndex)){
//            String innerContent = facAddrMatcher.group(1);
//            tmpClientDetails.tmpFacAddress = tmpAddressFromString(innerContent);
//            facAddrMatcher.group(0);
//            nextIndex = facAddrMatcher.end();
//        }
//
//        Pattern regAddr = Pattern.compile("<register(.*?)/>");
//        Matcher regAddrMatcher = regAddr.matcher(xmlBundle);
//        while (regAddrMatcher.find(nextIndex)){
//            String innerContent = regAddrMatcher.group(1);
//            tmpClientDetails.tmpRegAddress = tmpAddressFromString(innerContent);
//            regAddrMatcher.group(0);
//            nextIndex = regAddrMatcher.end();
//        }
//
//        System.out.println(tmpClientDetails);
//    }
//
//    static public TmpAddress tmpAddressFromString(String innerContent){
//
//        Pattern street = Pattern.compile("street=\"(.*?)\"");
//        Pattern house = Pattern.compile("house=\"(.*?)\"");
//        Pattern flat = Pattern.compile("flat=\"(.*?)\"");
//
//        TmpAddress  tmpAddress = new TmpAddress();
//        int nextInnerIndex = 0;
//
//        Matcher streetMatcher = street.matcher(innerContent);
//        while (streetMatcher.find(nextInnerIndex)){
//            tmpAddress.street = streetMatcher.group(1);
//            streetMatcher.group(0);
//            nextInnerIndex = streetMatcher.end();
//        }
//        Matcher houseMatcher = house.matcher(innerContent);
//        while (houseMatcher.find(nextInnerIndex)){
//            tmpAddress.house = houseMatcher.group(1);
//            houseMatcher.group(0);
//            nextInnerIndex = houseMatcher.end();
//        }
//
//        Matcher flatMatcher = flat.matcher(innerContent);
//        while (flatMatcher.find(nextInnerIndex)){
//            tmpAddress.flat = flatMatcher.group(1);
//            flatMatcher.group(0);
//            nextInnerIndex = flatMatcher.end();
//        }
//        return tmpAddress;
//    }
//
//    static public void main(String[] args) throws IOException {
//
//
//        File file = new File("D:\\greetgonstuff\\greetgo.sandbox\\cia.txt");
//        FileInputStream fis = new FileInputStream(file);
//        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
//        int a = 0;
//        String bundle;
//        while((bundle = createBundle(br)
//            )!=null){
//            System.out.println(bundle);
//            createTmpClient(bundle);
//            System.out.println(a+"\n\n");
//            a++;
//        }
//        br.close();
//        fis.close();
//
//
//
//    }
//
//}
