package demo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Demonstrate that any object can be proxied, no matter if the object implements any interfaces. A totally unrelated
 * interface can be used for the object, no need for it to match the object or declare any methods.
 * @author justi
 */
public class InvocationHandlerDemo implements InvocationHandler {
	private static interface EmptyInterface {/*empty*/}

	private final Object proxiedObject;

	// Replace default constructor to pass in a reference to any object (no interfaces required).
	public InvocationHandlerDemo(Object proxiedObject) {
		this.proxiedObject = proxiedObject;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		final String methodName = method.getName();
		System.out.println("*** Intercepted method " + methodName + " ***");
		if (null == this.proxiedObject) {
			throw new NullPointerException();
		} else if (methodName.equals("hashCode")) {
			return Integer.valueOf(0);	// override proxiedObject.hashCode()
		} else if (methodName.equals("equals")) {
			return Boolean.TRUE;		// override proxiedObject.equals()
		}
		return method.invoke(this.proxiedObject, args);	// pass-through other proxiedObject methods like toString()
	}

	public static Object create(Object object) {
		Object proxy = (Object) Proxy.newProxyInstance(
			InvocationHandlerDemo.class.getClassLoader(),
			new Class[] { EmptyInterface.class },	// interface(s) only, never classes
			new InvocationHandlerDemo(object)		// invocation wrapper for object
		);
		return proxy;
	}

	public static void main(String[] args) {
		Object object = new Object();
		Object proxiedObject = InvocationHandlerDemo.create(object);
		System.out.println("Original hashCode:  " + object.hashCode());			// not proxy
		System.out.println(" ");
		System.out.println("Proxied  hashCode:  " + proxiedObject.hashCode());	// proxied, and method is intercepted
		System.out.println(" ");
		System.out.println("Original toString   " + object.toString());			// not proxied
		System.out.println(" ");
		System.out.println("Proxied  toString:  " + proxiedObject.toString());	// proxied, but method is not intercepted
		System.out.println(" ");
		System.out.println("Original equals:    " + object.equals(null));			// not proxied
		System.out.println(" ");
		System.out.println("Proxied  equals:    " + proxiedObject.equals(null));	// proxied, but method is not intercepted
		System.out.println(" ");
	}
}