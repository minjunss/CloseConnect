package com.CloseConnect.closeconnect.security.oatuh2.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

public class CookieUtils {

    //특정 쿠키 찾는 메서드
    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    //특정 쿠키의 값을 리턴하는 메서드
    public static Optional<String> readServlet(HttpServletRequest request, String name) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> name.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findAny();
    }

    //Response 에 쿠키를 추가하는 메서드
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    //특정 쿠키 삭제하는 메서드
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    //객체를 직렬화한 후 Base64로 인코딩하여 문자열로 리턴하는 메서드
    public static String serialize(Object object) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(object));
    }

    //주어진 쿠키의 값을 역직렬화하고 해당 객체로 변환하는 메서드
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        //deprecated
        //return cls.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));

        byte[] data = Base64.getUrlDecoder().decode(cookie.getValue());
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return cls.cast(ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
