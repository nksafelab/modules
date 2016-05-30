package jinke.readings.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CDelegate {
	private Method method = null;
	private Object obj = null;
	private Class c = null;
	private String[] arrArgsName = null;
	private Class<?>[] classType = null;
	private int nDelegateType = 0;

	public CDelegate(Object obj, String szMethod, String... argsName)
			throws Exception {
		this.c = obj.getClass();
		if (0 == argsName.length) {
			this.arrArgsName = new String[0];
		} else {
			this.arrArgsName = argsName;
		}

		this.obj = obj;
		try {

			this.method = this.c.getMethod(szMethod, getClassType(argsName));
		} catch (Exception e) {
			throw new Exception("The method fail in initing!");
		}
		this.nDelegateType = 1;
	}

	public CDelegate(Object obj, String szMethod, Class<?>... argsType)
			throws Exception {
		this.c = obj.getClass();
		if (0 == argsType.length) {
			this.classType = new Class<?>[0];
		} else {
			this.classType = argsType;
		}

		this.obj = obj;
		try {

			this.method = this.c.getMethod(szMethod, argsType);
		} catch (Exception e) {
			throw new Exception("The method fail in initing!");
		}
		this.nDelegateType = 2;
	}

	public void invoke(Object... args) throws Exception {
		switch (this.nDelegateType) {
		case 1:
			if (args.length != this.arrArgsName.length) {
				throw new Exception(
						"The count of arguments can't match with the delegate!");
			} else {
				for (int i = 0; i < this.arrArgsName.length; i++) {
					if (!args[i].getClass().getName().equals(arrArgsName[i])) {
						throw new Exception(
								"The arguments can't match with the delegate!");
					}

				}

			}

			this.method.invoke(this.obj, args);
			break;
		case 2:
			if (args.length != this.classType.length) {
				throw new Exception(
						"The count of arguments can't match with the delegate!");
			} else {
				for (int i = 0; i < this.classType.length; i++) {
					if (args[i].getClass() != this.classType[i]) {
						throw new Exception(
								"The arguments can't match with the delegate!");
					}

				}

			}
			this.method.invoke(this.obj, args);
			break;
		default:
			break;

		}

	}

	private Class<?>[] getClassType(String[] arrName)
			throws ClassNotFoundException {
		int nCount = arrName.length;
		Class<?>[] arrClass = new Class<?>[nCount];
		for (int i = 0; i < nCount; i++) {
			arrClass[i] = Class.forName(arrName[i]);
		}
		return arrClass;
	}

	private boolean validateArgs() {

		return false;
	}

}
