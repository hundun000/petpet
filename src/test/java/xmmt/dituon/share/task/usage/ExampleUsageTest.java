package xmmt.dituon.share.task.usage;

import org.junit.Test;
import xmmt.dituon.share.task.PetpetBlockException;

import java.io.IOException;

import static org.junit.Assert.*;

public class ExampleUsageTest {

    static ExampleUsage exampleUsage = new ExampleUsage();

    @Test
    public void test_testcase0() throws PetpetBlockException, IOException {
       exampleUsage.work("./example-data/petpetBlock/testcase0");
    }

    @Test
    public void test_yosuganosora() throws PetpetBlockException, IOException {
        exampleUsage.work("./example-data/petpetBlock/yosuganosora");
    }
}