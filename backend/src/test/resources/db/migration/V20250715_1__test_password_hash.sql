UPDATE users
SET password_hash = '{noop}12345'
WHERE username = 'alice';

UPDATE users
SET password_hash = '{noop}54321'
WHERE username = 'bob';
