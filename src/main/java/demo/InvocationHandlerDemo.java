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
			System.out.println("*** Overriding method " + methodName + " ***");
			return Integer.valueOf(0);	// override proxiedObject.hashCode()
		} else if (methodName.equals("equals")) {
			System.out.println("*** Overriding method " + methodName + " ***");
			return Boolean.TRUE;		// override proxiedObject.equals()
		}
		System.out.println("*** Not overriding method " + methodName + " ***");
		return method.invoke(this.proxiedObject, args);	// pass-through other proxiedObject methods like toString()
	}

	public static Object create(Object objectWithoutInterface) {
		Object proxy = (Object) Proxy.newProxyInstance(			// apparently this works for the object even though it does not implement the interface
			InvocationHandlerDemo.class.getClassLoader(),
			new Class[] { EmptyInterface.class },				// interface(s) only, never concrete class(es)
			new InvocationHandlerDemo(objectWithoutInterface)	// invocation wrapper for object
		);
		return proxy;
	}

	public static void main(String[] args) {
		Object objectWithoutInterface = new Object();
		Object proxiedWithInterface = InvocationHandlerDemo.create(objectWithoutInterface);	// object does not need to implement interface used inside create?
		System.out.println("============================================================================================");
		System.out.println(" ");
		System.out.println("Original hashCode:  " + objectWithoutInterface.hashCode());		// not proxied
		System.out.println(" ");
		System.out.println("Proxied  hashCode:  " + proxiedWithInterface.hashCode());		// proxied, and method is intercepted
		System.out.println(" ");
		System.out.println("============================================================================================");
		System.out.println(" ");
		System.out.println("Original toString   " + objectWithoutInterface.toString());		// not proxied
		System.out.println(" ");
		System.out.println("Proxied  toString:  " + proxiedWithInterface.toString());		// proxied, but method is not intercepted
		System.out.println(" ");
		System.out.println("============================================================================================");
		System.out.println(" ");
		System.out.println("Original equals:    " + objectWithoutInterface.equals(null));	// not proxied
		System.out.println(" ");
		System.out.println("Proxied  equals:    " + proxiedWithInterface.equals(null));		// proxied, and method is intercepted
		System.out.println(" ");
		System.out.println("============================================================================================");
	}
}