/*
 * Copyright (c) 2021. Team CoderGram
 *
 * @author Emil Elkjær Nielsen (cph-en93@cphbusiness.dk)
 * @author Sigurd Arik Gaarde Nielsen (cph-at89@cphbusiness.dk)
 */

DROP USER IF EXISTS fogtest@localhost;
CREATE USER fogtest@localhost IDENTIFIED BY 'codergram';
GRANT ALL PRIVILEGES ON fogtest.* TO fogtest@localhost;