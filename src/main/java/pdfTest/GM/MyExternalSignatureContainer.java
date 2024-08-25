package pdfTest.GM;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.security.ExternalSignatureContainer;

import java.io.InputStream;

public class MyExternalSignatureContainer implements ExternalSignatureContainer {
	protected byte[] sig;

	public MyExternalSignatureContainer(byte[] sig) {
		this.sig = sig;
	}

	@Override
	public byte[] sign(InputStream is) {
		return sig;
	}

	@Override
	public void modifySigningDictionary(PdfDictionary signDic) {
	}
}
