package ru.astondevs.mycare.util;

import lombok.experimental.UtilityClass;

/**
 * Описание сообщений об ошибках для исключений.
 * <p>
 * Определяет текстовые сообщения для различных исключительных ситуаций.
 *
 * @author Mikhail Snikhovskii
 */
@UtilityClass
public class ExceptionMessage {

    public static final String DMS_PROGRAM_NOT_FOUND = "ДМС программы с таким идентификатором %s не существует!";
    public static final String DOCUMENT_NOT_FOUND = "Документа с таким идентификатором %s не существует!";
    public static final String INSURANCE_APPLICATION_NOT_FOUND = "Заявки с таким идентификатором %s не существует!";
    public static final String INSURED_PERSON_NOT_FOUND = "Страхуемого лица с таким идентификатором %s не существует!";
    public static final String POLICY_NOT_FOUND = "Полис с таким идентификатором %s не существует!";
    public static final String CLIENT_NOT_FOUND = "Клиента с таким идентификатором %s не существует!";
}
