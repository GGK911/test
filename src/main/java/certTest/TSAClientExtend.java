package certTest;

import cn.hutool.http.HttpRequest;
import com.itextpdf.text.error_messages.MessageLocalization;
import com.itextpdf.text.pdf.security.TSAClientBouncyCastle;
import com.itextpdf.text.pdf.security.TSAInfoBouncyCastle;
import org.bouncycastle.asn1.cmp.PKIFailureInfo;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenInfo;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

/**
 * @author TangHaoKai
 * @version V1.0 2023-11-15 10:12
 **/
public class TSAClientExtend extends TSAClientBouncyCastle {
    public TSAClientExtend(String url) {
        super(url);
    }

    public TSAClientExtend(String url, String username, String password) {
        super(url, username, password);
    }

    public TSAClientExtend(String url, String username, String password, int tokSzEstimate, String digestAlgorithm) {
        super(url, username, password, tokSzEstimate, digestAlgorithm);
    }

    @Override
    public void setTSAInfo(TSAInfoBouncyCastle tsaInfo) {
        super.setTSAInfo(tsaInfo);
    }

    @Override
    public int getTokenSizeEstimate() {
        return super.getTokenSizeEstimate();
    }

    @Override
    public MessageDigest getMessageDigest() throws GeneralSecurityException {
        return super.getMessageDigest();
    }

    @Override
    public byte[] getTimeStampToken(byte[] imprint) throws IOException, TSPException {
        byte[] respBytes = getTSAResponse(imprint);
        TimeStampResponse response = new TimeStampResponse(respBytes);
        response.validate(new TimeStampRequest(imprint));
        PKIFailureInfo failure = response.getFailInfo();
        int value = (failure == null) ? 0 : failure.intValue();
        if (value != 0) {
            throw new IOException(MessageLocalization.getComposedMessage("invalid.tsa.1.response.code.2", tsaURL, String.valueOf(value)));
        }
        TimeStampToken tsToken = response.getTimeStampToken();
        if (tsToken == null) {
            throw new IOException(MessageLocalization.getComposedMessage("tsa.1.failed.to.return.time.stamp.token.2", tsaURL, response.getStatusString()));
        }
        TimeStampTokenInfo tsTokenInfo = tsToken.getTimeStampInfo(); // to view details
        byte[] encoded = tsToken.getEncoded();

        if (tsaInfo != null) {
            tsaInfo.inspectTimeStampTokenInfo(tsTokenInfo);
        }
        this.tokenSizeEstimate = encoded.length + 32;
        return encoded;
    }

    @Override
    protected byte[] getTSAResponse(byte[] requestBytes) throws IOException {
        return HttpRequest
                .post(tsaURL)
                .header("Content-Type", "application/timestamp-query")
                .header("Content-Transfer-Encoding", "binary")
                .body(requestBytes)
                .execute()
                .bodyBytes();
    }
}
