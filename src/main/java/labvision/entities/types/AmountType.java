package labvision.entities.types;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import labvision.entities.Amount;

/**
 * Custom-defined type to persist parameterized Amount objects
 * @author davidnisson
 *
 */
public class AmountType implements UserType {
	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	@Override
	public Amount<?> disassemble(Object value) throws HibernateException {
		return (Amount<?>)value;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return x.equals(y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Amount<?> nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
			throws HibernateException, SQLException {
		double value = rs.getDouble(names[0]);
		
		if (rs.wasNull()) {
			return null;
		}
		
		double uncertainty = rs.getDouble(names[1]);
		
		return new Amount<>(value, uncertainty);
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
			throws HibernateException, SQLException {
		if (Objects.isNull(value)) {
			st.setNull(index, Types.DOUBLE);
		} else {
			Amount<?> amount = (Amount<?>) value;
			st.setDouble(index, amount.value);
			st.setDouble(index+1, amount.value);
		}
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}

	@Override
	public Class<?> returnedClass() {
		return Amount.class;
	}

	@Override
	public int[] sqlTypes() {
		return new int[] { Types.DOUBLE, Types.DOUBLE };
	}
	
}
