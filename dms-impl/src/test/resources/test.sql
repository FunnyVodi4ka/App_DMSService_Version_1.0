-- noinspection SqlWithoutWhereForFile

DELETE
FROM agreement;
DELETE
FROM dms_claim;
DELETE
FROM dms_insurance;
DELETE
FROM dms_program;
DELETE
FROM dms_service.confirmation_document;
DELETE
FROM dms_service.dependent_person;

insert into dms_program
values ('c331727e-511d-4177-963f-c1f033c23655', 'MAXIMUM', true, true,
        true, true, true);

insert into dms_insurance
values ('3f69f66f-4bd6-4e20-a175-07537322d967', '4e068238-b770-40ad-bf49-89c6effa4c3b',
        'c331727e-511d-4177-963f-c1f033c23655', 'c331727e-511d-4177-963f-c1f033c23655',
        'APPROVED', '2025-01-16 22:28:33.000000', '2025-02-16', '2026-03-16', false,
        'ONCE_IN_TWELVE_MONTHS', 42.00, 420.00, null);

insert into agreement
values ('40378373-0938-41ca-b18b-4ba1ac8fed02', '3f69f66f-4bd6-4e20-a175-07537322d967',
        '2025-01-16', true, 'PAID', 'abc-123');

insert into dms_claim
values ('3cc3fc56-44e6-485d-8e19-2412283670b4', '40378373-0938-41ca-b18b-4ba1ac8fed02',
        'ef874c9f-99b1-4672-8d2d-02d5ea974c27', 'PENDING', 'CLINIC_DETAILS',
        '67d744a16538fb4ff75ccd5c', '2025-03-13', 12345.67);

INSERT INTO dms_service.dependent_person
VALUES ('40580210-9c4b-4b70-9fdd-e3a742b00636', 'John', 'Doe',
        'Robert', 'MALE', '1970-01-01', 'Lakeview av. 1',
        'john.doe@nowher.orge', '+1007', 'PASSPORT',
        '42', '420', '2000-01-01',
        'Weyland-Yutani');

INSERT INTO dms_service.confirmation_document
VALUES ('d837c9d5-0baf-4698-aadb-2eff3ed770ee', 'confirmation.pdf',
        'PDF', 'https://example.org/doc.pdf',
        '40580210-9c4b-4b70-9fdd-e3a742b00636');
