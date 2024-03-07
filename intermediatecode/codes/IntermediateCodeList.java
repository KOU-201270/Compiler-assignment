package intermediatecode.codes;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

public class IntermediateCodeList extends ArrayList<IntermediateCode> {
    private int cnt;

    public IntermediateCodeList(int initialCapacity) {
        super(initialCapacity);
        cnt = 0;
    }

    public IntermediateCodeList() {
        cnt = 0;
    }

    public IntermediateCodeList(Collection<? extends IntermediateCode> c) {
        super(c);
        cnt = 0;
    }

    public void print(OutputStream output) throws IOException {
        for (IntermediateCode code : this) {
            output.write(code.toString().getBytes(StandardCharsets.UTF_8));
        }
    }
}
