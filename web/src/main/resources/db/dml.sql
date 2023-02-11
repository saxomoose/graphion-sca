insert into eid.users (id, username, password)
values (1, 'admin@graphion.test', 'password');

insert into eid.closings (id, name, date)
values
    (1, 'demo_closing_1', '2023-01-01'),
    (2, 'demo_closing_2', '2023-01-01');

insert into eid.legal_persons (id, enterprise_number, function, status)
values
    (1, 502465839, 1, false);

insert into eid.signatories (id, dtype, first_name, last_name, email, national_registration_number, legal_person_id, function, enterprise_number_permanent_representative)
values
    (1, 'Signatory', 'Mathieu', 'Tulpinck', 'mathieu.tulpinck@gmail.com', 90110842343, null, null, null),
    (2, 'Signatory', 'Guibert', 'Tulpinck', 'guibert.tulpinck@gmail.com', 46052125865, null, null, null),
    (3, 'OfficerSignatory', 'Dimitri', 'Pirnary', 'dimitri.pirnay@gmail.com', 88120232365, 1, 1, null),
    (4, 'OfficerSignatory', 'Guido', 'Beazar', 'guido.beazar@gmail.com', 50120232365, 1, 2, 475697995);

insert into eid.documents (id, closing_id, filename, path, created_at, modified_at)
values
    (1, 1, 'document_1.txt', '/home/ubuntu/storage/document_1.txt', '2023-01-01T00:00:00.00Z', '2023-01-01T00:00:00.00Z'),
    (2, 1, 'document_2.txt', '/home/ubuntu/storage/document_2.txt', '2023-01-01T00:00:00.00Z', '2023-01-01T00:00:00.00Z');

insert into eid.closing_signatory (closing_id, signatory_id, document_id, signature_timestamp)
values
    (1, 1, 1, '2023-01-01T00:00:00.00Z'),
    (1, 1, 2, '2023-01-01T00:00:00.00Z'),
    (1, 2, 1, '2023-01-01T00:00:00.00Z'),
    (1, 2, 2, null);
