insert into eid.users (id, username, password)
values (1, 'admin@graphion.test', 'password');

insert into eid.closings (id, date)
values
    (1, '2023-01-01'),
    (2, '2023-01-01');

insert into eid.signatories (id, first_name, last_name, email, national_registration_number)
values
    (1, 'Mathieu', 'Tulpinck', 'mathieu.tulpinck@gmail.com', 90110842343),
    (2, 'Guibert', 'Tulpinck', 'guibert.tulpinck@gmail.com', 46052125865);

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
