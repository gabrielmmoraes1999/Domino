package modelo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import view.TelaErro;

/**
 *
 * @author Gabriel Moraes
 */
public class ComputerDAO {
    
    public static String getMotherboardManufacturer() {
        String result = null;
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            try (FileWriter fw = new java.io.FileWriter(file)) {
                String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                        + "Set colItems = objWMIService.ExecQuery _ \n"
                        + "   (\"Select * from Win32_BaseBoard\") \n"
                        + "For Each objItem in colItems \n"
                        + "    Wscript.Echo objItem.Manufacturer \n"
                        + "    exit for  ' do the first cpu only! \n" + "Next \n";
                
                fw.write(vbs);
            }
            
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                result = input.readLine();
            }
        } catch (IOException ex) {
            new TelaErro(3, ex.getStackTrace()).setVisible(true);
        }
        return result;
    }
    
    public static String getMotherboardName() {
        String result = null;
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            try (FileWriter fw = new java.io.FileWriter(file)) {
                String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                        + "Set colItems = objWMIService.ExecQuery _ \n"
                        + "   (\"Select * from Win32_BaseBoard\") \n"
                        + "For Each objItem in colItems \n"
                        + "    Wscript.Echo objItem.Name \n"
                        + "    exit for  ' do the first cpu only! \n" + "Next \n";
                
                fw.write(vbs);
            }
            
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                result = input.readLine();
            }
        } catch (IOException ex) {
            new TelaErro(3, ex.getStackTrace()).setVisible(true);
        }
        return result;
    }
    
    public static String getMotherboardSerialNumber() {
        String result = null;
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            try (FileWriter fw = new java.io.FileWriter(file)) {
                String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                        + "Set colItems = objWMIService.ExecQuery _ \n"
                        + "   (\"Select * from Win32_BaseBoard\") \n"
                        + "For Each objItem in colItems \n"
                        + "    Wscript.Echo objItem.SerialNumber \n"
                        + "    exit for  ' do the first cpu only! \n" + "Next \n";
                
                fw.write(vbs);
            }
            
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                result = input.readLine();
            }
        } catch (IOException ex) {
            new TelaErro(3, ex.getStackTrace()).setVisible(true);
        }
        return result;
    }
    
    public static String getMotherboardVersion() {
        String result = null;
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            try (FileWriter fw = new java.io.FileWriter(file)) {
                String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                        + "Set colItems = objWMIService.ExecQuery _ \n"
                        + "   (\"Select * from Win32_BaseBoard\") \n"
                        + "For Each objItem in colItems \n"
                        + "    Wscript.Echo objItem.Version \n"
                        + "    exit for  ' do the first cpu only! \n" + "Next \n";
                
                fw.write(vbs);
            }
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                result = input.readLine();
            }
        } catch (IOException ex) {
            new TelaErro(3, ex.getStackTrace()).setVisible(true);
        }
        return result;
    }
    
    public static String getCPUName() {
        String result = "";
        try {
            File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            try (FileWriter fw = new java.io.FileWriter(file)) {
                String vbs = "On Error Resume Next \r\n\r\n"
                        + "strComputer = \".\"  \r\n"
                        + "Set objWMIService = GetObject(\"winmgmts:\" _ \r\n"
                        + "    & \"{impersonationLevel=impersonate}!\\\\\" & strComputer & \"\\root\\cimv2\") \r\n"
                        + "Set colItems = objWMIService.ExecQuery(\"Select * from Win32_Processor\")  \r\n "
                        + "For Each objItem in colItems\r\n "
                        + "    Wscript.Echo objItem.Name  \r\n "
                        + "    exit for  ' do the first cpu only! \r\n"
                        + "Next                    ";
                
                fw.write(vbs);
            }
            
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = input.readLine()) != null) {
                    result += line;
                }
            }
        } catch (IOException ex) {
            new TelaErro(3, ex.getStackTrace()).setVisible(true);
        }
        if (result.trim().length() < 1) {
            result = "NO_CPU_ID";
        }
        return result.trim();
    }
    
    public static String getCPUSerial() {
        String result = "";
        try {
            File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            try (FileWriter fw = new java.io.FileWriter(file)) {
                String vbs = "On Error Resume Next \r\n\r\n"
                        + "strComputer = \".\"  \r\n"
                        + "Set objWMIService = GetObject(\"winmgmts:\" _ \r\n"
                        + "    & \"{impersonationLevel=impersonate}!\\\\\" & strComputer & \"\\root\\cimv2\") \r\n"
                        + "Set colItems = objWMIService.ExecQuery(\"Select * from Win32_Processor\")  \r\n "
                        + "For Each objItem in colItems\r\n "
                        + "    Wscript.Echo objItem.ProcessorId  \r\n "
                        + "    exit for  ' do the first cpu only! \r\n"
                        + "Next                    ";
                
                fw.write(vbs);
            }
            
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = input.readLine()) != null) {
                    result += line;
                }
            }
        }  catch (IOException ex) {
            new TelaErro(3, ex.getStackTrace()).setVisible(true);
        }
        if (result.trim().length() < 1) {
            result = "NO_CPU_ID";
        }
        return result.trim();
    }

}
