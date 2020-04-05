import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.*;

public class homework {
    public static int queryNum;
    public static int kbNum;
    public static String[] query;
    public static String[] kb;
    public static ArrayList<String> KB = new ArrayList<>();
   // public static HashMap<String,Character> map = new HashMap<>();

    public static void main(String[] args) throws Exception {
        read();
        standardLize();
        twoRoution();
        File outputFile = new File("output.txt");
        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }
        Writer writer = new FileWriter(outputFile);
        BufferedWriter bw = new BufferedWriter(writer);


        //test resolution
        String t1 = "~Take(x,Warfarin)";
        String t2 = "Take(Bob)";
        String res = resolution(t1,t2);
        System.out.println(res + "  res0");



//        //test get elements
//        List<String> an = getSpilt("() ~Take(Bob,Antacids) HighBP(Bob)");
//        for(int i = 0; i< an.size(); i++){
//            System.out.println("an  " + an.get(i));
//        }

//        String test = getRest("() ~Take(Bob,Antacids) HighBP(Bob)", "()");
//
//        System.out.println("test:" + test);

        writeOutput(bw);
        bw.close();
        writer.close();
    }

    public static void writeOutput( BufferedWriter bw) throws Exception{
        boolean[] res = compare();
        for(int i = 0;  i < res.length;i++){
            if(res[i]){
                bw.write("TRUE");
            }else{
                bw.write("FALSE");
            }
            if(i!= res.length-1){
                bw.newLine();
            }

        }
    }


    public static void standardLize(){
        for(int i = 0; i < kb.length; i++){
            int index = kb[i].indexOf("=>");
            if(index >=0){
                int num = 0;
                String str = "";
                for(int j = 0; j < kb[i].length(); j++){

                    if(kb[i].charAt(j) == ')'  && j< index){
                        String temp = kb[i].substring(num, j+1);
                        if(temp.charAt(0) != '~'){  //去重
                            str = str + "~" + temp + " " ;
                        }else{
                            str = str + temp.substring(1) + " ";
                        }
                        //KB.add(negTemp);
                    }else if(kb[i].charAt(j) == ' ' || kb[i].charAt(j) == '&'  ) {
                        num = j + 1;
                        continue;
                    }else if(kb[i].charAt(j) == ')'  && j> index){
                        String temp = kb[i].substring(num, j+1);
                        str = str + temp + " ";
                        //KB.add(temp);
                    }

                }
                KB.add(str);
            }else{  //不存在=》
                KB.add(kb[i]);
                // KB.add("&");
            }
        }
    }

    public static void twoRoution(){
        for(int i = 1; i< KB.size(); i++){
            for(int j = 0; j< i; j++){
                String res = resolution(KB.get(i),KB.get(j));
                if(res.equals("FAIL")){
                    continue;
                }else{
                    if(!KB.contains(res)){
                        KB.add(res);
                    }
//                    KB.remove(i);
//                    KB.remove(j);
                }

            }
            if(KB.size() > 3000){
                return;
            }

        }

    }

    //抵消相同的
    public static String resolution(String str1, String str2){
        String[] res = new String[2];
        boolean change = false;
        ArrayList<String> subList = new ArrayList<>();
        String[] string1 = str1.split(" ");
        String[] string2 = str2.split(" ");


        for(int i = 0; i< string1.length; i++){
            for(int j = 0; j< string2.length; j++) {
                if(string1[i].equals("") || string2[j].equals("")){
                    continue;
                }
                boolean t1 = string1[i].charAt(0) == '~';
                //System.out.println(string2[j].length());
                boolean t2 = string2[j].charAt(0) == '~';
                if(t1 == t2){
                    continue;
                }
                String temp1 ="";
                String temp2 ="";
                if(t1) {
                    temp1 = string1[i].substring(1);
                    temp2 = string2[j];
                }
                if(t2){
                    temp2 = string2[j].substring(1);
                    temp1 = string1[i];

                }
                if (checkIfunify(temp1, temp2,subList)) {
                    //System.out.println("unify ");
                    change = true;
                    string1[i] = "";
                    string2[j] = "";
                }
            }
        }
        if(!change) return "FAIL";
        res[0]=  deleteNull(string1);
        res[1] = deleteNull(string2);
        res[0] = res[0].trim();
        res[1] = res[1].trim();
        for(int i = 0; i < subList.size(); i++){
            int index = subList.get(i).indexOf('/');
            res[0] = res[0].replaceAll(subList.get(i).substring(index+1),subList.get(i).substring(0,index));
            res[1] = res[1].replaceAll(subList.get(i).substring(index+1),subList.get(i).substring(0,index));
        }

        if(res[0].equals("")) {
            return res[1];
        }
        if(res[1].equals("")) {
            return res[0];
        }
        String temp = res[1] + " " +res[0];
        String[] stringT = temp.split(" ");
        for(int i = 0; i< stringT.length-1; i++){
            for(int j = i+1; j < stringT.length; j++){
                if(stringT[i].equals(stringT[j])){
                    stringT[j] = "";
                }
            }
        }

        temp = deleteNull(stringT);
        return temp.trim();

    }

    public static String deleteNull(String[] string1){
        StringBuilder stringBu = new StringBuilder();
        for(int i = 0; i< string1.length; i++){
            if(string1[i] == null) continue;
            stringBu.append(string1[i]);
            if(i!= string1.length-1) {
                stringBu.append(" ");
            }
        }

        return stringBu.toString().trim();
    }


    public static boolean checkIfunify(String str1, String str2, ArrayList<String> subList ){
        List<String> list1 =getSpilt(str1);
        List<String> list2 =getSpilt(str2);
        String res = unify(str1,str2,list1,list2,subList);
        if(res.equals("S")) return true;
        return false;


    }

    private static ArrayList<String> getSpilt(String str){
        ArrayList<String> res = new ArrayList<>();
        int j = 0;
        for(int i = 0; i < str.length(); i++){
            if((str.charAt(i) >= 'a' && str.charAt(i) <= 'z') || (str.charAt(i) >= 'A' && str.charAt(i) <= 'Z')){
                j = i;
                while(j < str.length() && ((str.charAt(j) >= 'a' && str.charAt(j) <= 'z') || (str.charAt(j) >= 'A' && str.charAt(j) <= 'Z')) ||(str.charAt(j) >= '0' && str.charAt(j) <= '9')) {
                    j++;
                }
                //j come to )
                res.add(str.substring(i,j));//取出'('和')'之间的串
                i = j-1;
            }
            if(str.charAt(i) == ')') {
                res.add("()");
            }
        }
        return res;
    }

    public static String unify(String E1, String E2, List<String> list1 , List<String> list2, ArrayList<String> substitutionList ){
        if(E1.isEmpty() && E2.isEmpty())
            return " ";
        if(constants(E1) && constants(E2)){
            if(E1.equals(E2))
                return " ";
            else
                return "F";
        }
        if(variable(E1)) {
            substitutionList.add(E2 + "/" + E1);
            return "S";
        }
        if(variable(E2)){
            substitutionList.add(E1 + "/" + E2);
            return "S";
        }
        if(E1.isEmpty()|| E2.isEmpty()) {
            return "F";
        }
        String first1 = getFirst(list1);
        E1 = getRest(E1, first1);
        String first2 = getFirst(list2);
        E2 = getRest(E2, first2);
        String prevString= unify(first1, first2,list1,list2,substitutionList);
        if (prevString.equals("F")) {
            return "F";
        }
        String postString = unify(E1, E2,list1,list2,substitutionList);
        if (postString.equals("F")) {
            return "F";
        } else {
            return "S";
        }
    }

    public static String getRest(String str, String first){
        if(str.isEmpty()) return null;
        String temp;
        if(first.equals("()")) {
            temp= str.replaceFirst("\\(\\)", "]");
        }else{
            temp = str.replaceFirst(first,"]");
        }
        //System.out.println(temp);
        if(temp.length() == 1) {   //()
            return "";
        }
        int i = temp.indexOf(']');
        int j = i+1;
        while(temp.charAt(j) == ' ' || temp.charAt(j) == ',') {
            j++;
        }
        //System.out.println(i);
        if(i==0) return temp.substring(j);
        return temp.substring(0, i) + temp.substring(j);

    }



    public static String getFirst(List<String> list){
       // System.out.println(list.size()+ "list,size");
        String temp = list.get(0);
        list.remove(0);
        return temp;

    }

    //判断常量
    private static boolean constants(String str){
        if(str.isEmpty()) return false;
        if(str.equals("()")) return true;
        if(!(str.charAt(0)>='A') ||!( str.charAt(0) <= 'Z')|| str.contains(" ") || str.contains("(") || str.contains(")") || str.contains(",")) {
            return false;
        }
        return true;
    }

    private static boolean variable(String str){
        if(str.isEmpty()) return false;
        if(str.length() <= 1){
            if(str.charAt(0)>='a' && str.charAt(0) <= 'z')
                return true;
        }
        return false;

    }



    //做之前要更新这个KB
    public static boolean[] compare(){
        boolean[] res = new boolean[query.length];
        String temp = "";
        for(int j = 0; j< query.length;j++){
            if(query[j].charAt(0) =='~' ){
                temp = query[j].substring(1);
            }else{
                temp = "~" + query[j];
            }
            for(int i = 0; i < KB.size(); i++){
                if(resolution(KB.get(i), temp).equals("")){
                    res[j] = true;
                }
            }
//        for(int i = 0; i< query.length; i++){
//            if(KB.contains(query[i])){
//                res[i] = true;
//            }else{
//                res[i] = false;
//            }
        }
       return res;
    }


    public static void read() throws Exception {
        File inputFile = new File("input49.txt");
        Scanner in = new Scanner(inputFile);
        if (!inputFile.exists()) {
            throw new Exception("File does not exist!");
        }

        queryNum = in.nextInt();   //read line one
        in.nextLine();
        query = new String[queryNum];
        for(int i = 0; i <  queryNum; i++){
            query[i] = in.nextLine();
        }
        kbNum = in.nextInt();
        in.nextLine();
        kb = new String[kbNum];
        for(int i = 0; i < kbNum; i++){
            kb[i] = in.nextLine();
        }
    }
}
