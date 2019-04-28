import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImportAudio
{
    public static void main(String [] args) throws FileNotFoundException, IOException
    {
        FileInputStream file = new FileInputStream("./08. Through Glass.mp3");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte [] buffer = new byte [8192];
        int bytesIn;

        while ((bytesIn = file.read(buffer)) > 0)
        {
            output.write(buffer, 0, bytesIn);
        }

        file.close();

        for (byte b : buffer)
        {
            System.out.println(b);
        }

    }
}