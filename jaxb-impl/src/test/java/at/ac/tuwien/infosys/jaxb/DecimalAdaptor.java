package at.ac.tuwien.infosys.jaxb;

import java.math.BigDecimal;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.sun.xml.bind.WhiteSpaceProcessor;

/**
 * See https://github.com/whummer/jaxb-facets/issues/24
 */
public class DecimalAdaptor extends XmlAdapter<String, BigDecimal> {

	@Override
	public String marshal(BigDecimal value) throws Exception {
		if (value == null) {
			return null;
		}

		return value.toPlainString();
	}

	@Override
	public BigDecimal unmarshal(String value) throws Exception {
		if (value == null) {
			return null;
		}

		CharSequence trimmed = WhiteSpaceProcessor.trim(value);

		if (trimmed.length() == 0) {
			return null;
		}

		try {
			return new BigDecimal(trimmed.toString());
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Invalid decimal");
		}
	}
}