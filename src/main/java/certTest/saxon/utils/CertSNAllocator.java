package certTest.saxon.utils;

import java.math.BigInteger;

public interface CertSNAllocator {
    BigInteger nextSerialNumber() throws Exception;
}
