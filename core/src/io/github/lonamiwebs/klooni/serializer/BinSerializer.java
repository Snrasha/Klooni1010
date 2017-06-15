package io.github.lonamiwebs.klooni.serializer;

import java.util.Arrays;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import codesmells.annotations.LM;
import java.io.OutputStream;

public class BinSerializer {
    private static byte[] HEADER = new byte[]{ 107 , 108 , 111 , 111 , 110 , 105 , 10 };

    private static int VERSION = 2;

    public static void serialize(final BinSerializable serializable, final OutputStream output) throws IOException {
        DataOutputStream out = new DataOutputStream(output);
        try {
            out.write(BinSerializer.HEADER);
            out.writeInt(BinSerializer.VERSION);
            serializable.write(out);
        } finally {
            try {
                out.close();
            } catch (IOException ignored) {
            }
        }
    }

    @LM
    public static void deserialize(final BinSerializable serializable, final InputStream input) throws IOException {
        DataInputStream in = new DataInputStream(input);
        try {
            byte[] savedBuffer = new byte[BinSerializer.HEADER.length];
            in.readFully(savedBuffer);
            if (!(Arrays.equals(savedBuffer, BinSerializer.HEADER)))
                throw new IOException("Invalid saved header found.");
            
            int savedVersion = in.readInt();
            if (savedVersion != (BinSerializer.VERSION)) {
                throw new IOException(((("Invalid saved version found. Should be " + (BinSerializer.VERSION)) + ", not ") + savedVersion));
            }
            serializable.read(in);
        } finally {
            try {
                in.close();
            } catch (IOException ignored) {
            }
        }
    }
}

