--
-- PostgreSQL database dump
--

\restrict sZ8w2sZvHK4eTf3bfKMkoiRuJG7llUQECNX6R2Z4PIilHmEaeOZWAyLnXtwqabT

-- Dumped from database version 18.4 (72c6e7c)
-- Dumped by pg_dump version 18.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: cart_items; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.cart_items (
    item_cart_id bigint NOT NULL,
    cart_id bigint NOT NULL,
    product_variant_id integer NOT NULL,
    quantity integer NOT NULL,
    CONSTRAINT cart_items_quantity_check CHECK ((quantity > 0))
);


ALTER TABLE public.cart_items OWNER TO neondb_owner;

--
-- Name: cart_items_item_cart_id_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.cart_items_item_cart_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.cart_items_item_cart_id_seq OWNER TO neondb_owner;

--
-- Name: cart_items_item_cart_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.cart_items_item_cart_id_seq OWNED BY public.cart_items.item_cart_id;


--
-- Name: carts; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.carts (
    cart_id bigint NOT NULL,
    user_id bigint,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.carts OWNER TO neondb_owner;

--
-- Name: carts_cart_id_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.carts_cart_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.carts_cart_id_seq OWNER TO neondb_owner;

--
-- Name: carts_cart_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.carts_cart_id_seq OWNED BY public.carts.cart_id;


--
-- Name: categories; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.categories (
    id_category integer NOT NULL,
    name_category character varying(255) NOT NULL,
    is_active boolean DEFAULT true
);


ALTER TABLE public.categories OWNER TO neondb_owner;

--
-- Name: categories_id_category_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.categories_id_category_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.categories_id_category_seq OWNER TO neondb_owner;

--
-- Name: categories_id_category_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.categories_id_category_seq OWNED BY public.categories.id_category;


--
-- Name: colors; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.colors (
    id_color integer NOT NULL,
    name_color character varying(255) NOT NULL
);


ALTER TABLE public.colors OWNER TO neondb_owner;

--
-- Name: colors_id_color_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.colors_id_color_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.colors_id_color_seq OWNER TO neondb_owner;

--
-- Name: colors_id_color_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.colors_id_color_seq OWNED BY public.colors.id_color;


--
-- Name: payment_statuses; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.payment_statuses (
    id_status integer NOT NULL,
    status_name character varying(50) NOT NULL
);


ALTER TABLE public.payment_statuses OWNER TO neondb_owner;

--
-- Name: payment_statuses_id_status_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.payment_statuses_id_status_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.payment_statuses_id_status_seq OWNER TO neondb_owner;

--
-- Name: payment_statuses_id_status_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.payment_statuses_id_status_seq OWNED BY public.payment_statuses.id_status;


--
-- Name: payments; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.payments (
    id_payment bigint NOT NULL,
    id_sale bigint NOT NULL,
    payment_method character varying(50) NOT NULL,
    amount numeric(10,2) NOT NULL,
    payment_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    id_status integer
);


ALTER TABLE public.payments OWNER TO neondb_owner;

--
-- Name: payments_id_payment_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.payments_id_payment_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.payments_id_payment_seq OWNER TO neondb_owner;

--
-- Name: payments_id_payment_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.payments_id_payment_seq OWNED BY public.payments.id_payment;


--
-- Name: product_tags; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.product_tags (
    id_product integer NOT NULL,
    id_tag integer NOT NULL
);


ALTER TABLE public.product_tags OWNER TO neondb_owner;

--
-- Name: product_variants; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.product_variants (
    id_variant integer NOT NULL,
    id_product integer NOT NULL,
    id_size integer NOT NULL,
    id_color integer NOT NULL,
    stock integer DEFAULT 0 NOT NULL,
    is_active boolean DEFAULT true,
    image_url character varying(255)
);


ALTER TABLE public.product_variants OWNER TO neondb_owner;

--
-- Name: product_variants_id_variant_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.product_variants_id_variant_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.product_variants_id_variant_seq OWNER TO neondb_owner;

--
-- Name: product_variants_id_variant_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.product_variants_id_variant_seq OWNED BY public.product_variants.id_variant;


--
-- Name: products; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.products (
    id_product integer NOT NULL,
    name_product character varying(255) NOT NULL,
    price numeric(38,2) NOT NULL,
    is_active boolean DEFAULT true,
    created_at date DEFAULT CURRENT_TIMESTAMP,
    id_category integer,
    is_featured boolean DEFAULT false,
    discount_percentage integer DEFAULT 0
);


ALTER TABLE public.products OWNER TO neondb_owner;

--
-- Name: products_id_product_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.products_id_product_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.products_id_product_seq OWNER TO neondb_owner;

--
-- Name: products_id_product_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.products_id_product_seq OWNED BY public.products.id_product;


--
-- Name: roles; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.roles (
    id_role integer NOT NULL,
    name_role character varying(50) NOT NULL
);


ALTER TABLE public.roles OWNER TO neondb_owner;

--
-- Name: roles_id_role_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.roles_id_role_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.roles_id_role_seq OWNER TO neondb_owner;

--
-- Name: roles_id_role_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.roles_id_role_seq OWNED BY public.roles.id_role;


--
-- Name: sale_details; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.sale_details (
    id_sale_detail integer NOT NULL,
    id_sale integer NOT NULL,
    id_variant integer CONSTRAINT sale_details_id_product_not_null NOT NULL,
    quantity integer NOT NULL,
    unit_price numeric(10,2) NOT NULL,
    total_price numeric(10,2) NOT NULL,
    CONSTRAINT sale_details_quantity_check CHECK ((quantity > 0)),
    CONSTRAINT sale_details_total_price_check CHECK ((total_price >= (0)::numeric)),
    CONSTRAINT sale_details_unit_price_check CHECK ((unit_price >= (0)::numeric))
);


ALTER TABLE public.sale_details OWNER TO neondb_owner;

--
-- Name: sale_details_id_sale_detail_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.sale_details_id_sale_detail_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.sale_details_id_sale_detail_seq OWNER TO neondb_owner;

--
-- Name: sale_details_id_sale_detail_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.sale_details_id_sale_detail_seq OWNED BY public.sale_details.id_sale_detail;


--
-- Name: sale_statuses; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.sale_statuses (
    id_status integer NOT NULL,
    name_status character varying(50) NOT NULL
);


ALTER TABLE public.sale_statuses OWNER TO neondb_owner;

--
-- Name: sale_statuses_id_status_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.sale_statuses_id_status_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.sale_statuses_id_status_seq OWNER TO neondb_owner;

--
-- Name: sale_statuses_id_status_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.sale_statuses_id_status_seq OWNED BY public.sale_statuses.id_status;


--
-- Name: sales; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.sales (
    id_sale integer NOT NULL,
    id_user integer NOT NULL,
    sale_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    subtotal numeric(10,2) DEFAULT 0 NOT NULL,
    total numeric(10,2) DEFAULT 0 NOT NULL,
    id_status integer DEFAULT 1,
    status_report text,
    shipping_address text,
    CONSTRAINT sales_subtotal_check CHECK ((subtotal >= (0)::numeric)),
    CONSTRAINT sales_total_check CHECK ((total >= (0)::numeric))
);


ALTER TABLE public.sales OWNER TO neondb_owner;

--
-- Name: sales_id_sale_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.sales_id_sale_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.sales_id_sale_seq OWNER TO neondb_owner;

--
-- Name: sales_id_sale_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.sales_id_sale_seq OWNED BY public.sales.id_sale;


--
-- Name: sizes; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.sizes (
    id_size integer NOT NULL,
    name_size character varying(255) NOT NULL
);


ALTER TABLE public.sizes OWNER TO neondb_owner;

--
-- Name: sizes_id_size_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.sizes_id_size_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.sizes_id_size_seq OWNER TO neondb_owner;

--
-- Name: sizes_id_size_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.sizes_id_size_seq OWNED BY public.sizes.id_size;


--
-- Name: tags; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.tags (
    id_tag integer NOT NULL,
    name_tag character varying(255) NOT NULL
);


ALTER TABLE public.tags OWNER TO neondb_owner;

--
-- Name: tags_id_tag_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.tags_id_tag_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tags_id_tag_seq OWNER TO neondb_owner;

--
-- Name: tags_id_tag_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.tags_id_tag_seq OWNED BY public.tags.id_tag;


--
-- Name: users; Type: TABLE; Schema: public; Owner: neondb_owner
--

CREATE TABLE public.users (
    id_user bigint NOT NULL,
    first_name character varying(100) NOT NULL,
    last_name character varying(100) NOT NULL,
    email character varying(150) NOT NULL,
    phone_number character varying(20),
    password_hash character varying(255) NOT NULL,
    id_role integer NOT NULL,
    is_active boolean DEFAULT true,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    address text,
    last_access timestamp without time zone
);


ALTER TABLE public.users OWNER TO neondb_owner;

--
-- Name: users_id_user_seq; Type: SEQUENCE; Schema: public; Owner: neondb_owner
--

CREATE SEQUENCE public.users_id_user_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_id_user_seq OWNER TO neondb_owner;

--
-- Name: users_id_user_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: neondb_owner
--

ALTER SEQUENCE public.users_id_user_seq OWNED BY public.users.id_user;


--
-- Name: cart_items item_cart_id; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.cart_items ALTER COLUMN item_cart_id SET DEFAULT nextval('public.cart_items_item_cart_id_seq'::regclass);


--
-- Name: carts cart_id; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.carts ALTER COLUMN cart_id SET DEFAULT nextval('public.carts_cart_id_seq'::regclass);


--
-- Name: categories id_category; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.categories ALTER COLUMN id_category SET DEFAULT nextval('public.categories_id_category_seq'::regclass);


--
-- Name: colors id_color; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.colors ALTER COLUMN id_color SET DEFAULT nextval('public.colors_id_color_seq'::regclass);


--
-- Name: payment_statuses id_status; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.payment_statuses ALTER COLUMN id_status SET DEFAULT nextval('public.payment_statuses_id_status_seq'::regclass);


--
-- Name: payments id_payment; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.payments ALTER COLUMN id_payment SET DEFAULT nextval('public.payments_id_payment_seq'::regclass);


--
-- Name: product_variants id_variant; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.product_variants ALTER COLUMN id_variant SET DEFAULT nextval('public.product_variants_id_variant_seq'::regclass);


--
-- Name: products id_product; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.products ALTER COLUMN id_product SET DEFAULT nextval('public.products_id_product_seq'::regclass);


--
-- Name: roles id_role; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.roles ALTER COLUMN id_role SET DEFAULT nextval('public.roles_id_role_seq'::regclass);


--
-- Name: sale_details id_sale_detail; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.sale_details ALTER COLUMN id_sale_detail SET DEFAULT nextval('public.sale_details_id_sale_detail_seq'::regclass);


--
-- Name: sale_statuses id_status; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.sale_statuses ALTER COLUMN id_status SET DEFAULT nextval('public.sale_statuses_id_status_seq'::regclass);


--
-- Name: sales id_sale; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.sales ALTER COLUMN id_sale SET DEFAULT nextval('public.sales_id_sale_seq'::regclass);


--
-- Name: sizes id_size; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.sizes ALTER COLUMN id_size SET DEFAULT nextval('public.sizes_id_size_seq'::regclass);


--
-- Name: tags id_tag; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.tags ALTER COLUMN id_tag SET DEFAULT nextval('public.tags_id_tag_seq'::regclass);


--
-- Name: users id_user; Type: DEFAULT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.users ALTER COLUMN id_user SET DEFAULT nextval('public.users_id_user_seq'::regclass);


--
-- Data for Name: cart_items; Type: TABLE DATA; Schema: public; Owner: neondb_owner
--

COPY public.cart_items (item_cart_id, cart_id, product_variant_id, quantity) FROM stdin;
\.


--
-- Data for Name: carts; Type: TABLE DATA; Schema: public; Owner: neondb_owner
--

COPY public.carts (cart_id, user_id, created_at) FROM stdin;
\.


--
-- Data for Name: categories; Type: TABLE DATA; Schema: public; Owner: neondb_owner
--

COPY public.categories (id_category, name_category, is_active) FROM stdin;
2	Pantalones	t
3	Accesorios	t
1	Ropa Superior	t
\.


--
-- Data for Name: colors; Type: TABLE DATA; Schema: public; Owner: neondb_owner
--

COPY public.colors (id_color, name_color) FROM stdin;
1	Negro
2	Rojo
3	Azul
4	Blanco
6	Beige
\.


--
-- Data for Name: payment_statuses; Type: TABLE DATA; Schema: public; Owner: neondb_owner
--

COPY public.payment_statuses (id_status, status_name) FROM stdin;
1	APPROVED
2	REFUNDED
3	PENDING
4	FAILED
\.


--
-- Data for Name: payments; Type: TABLE DATA; Schema: public; Owner: neondb_owner
--

COPY public.payments (id_payment, id_sale, payment_method, amount, payment_date, id_status) FROM stdin;
5	16	BANK	235000.00	2026-06-05 04:06:27.821132	1
\.


--
-- Data for Name: product_tags; Type: TABLE DATA; Schema: public; Owner: neondb_owner
--

COPY public.product_tags (id_product, id_tag) FROM stdin;
\.


--
-- Data for Name: product_variants; Type: TABLE DATA; Schema: public; Owner: neondb_owner
--

COPY public.product_variants (id_variant, id_product, id_size, id_color, stock, is_active, image_url) FROM stdin;
42	20	4	6	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780631672/prahmoalmaaez5ouperf.png
43	21	7	1	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780631811/qxnwai4mstgbepuz9jqm.png
44	21	7	2	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780631824/jhqtqhkeogj70e0h21gm.png
45	21	7	3	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780631835/rayzng5aiuev5cgq1ogg.png
47	22	2	1	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780631920/tdxpx38ylc6dvzp3plen.png
48	22	3	1	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780631920/tdxpx38ylc6dvzp3plen.png
49	22	4	1	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780631920/tdxpx38ylc6dvzp3plen.png
50	22	1	4	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780631954/i6rlw0zxvzijir9kdmds.png
51	22	2	4	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780631954/i6rlw0zxvzijir9kdmds.png
52	22	3	4	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780631954/i6rlw0zxvzijir9kdmds.png
53	22	4	4	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780631954/i6rlw0zxvzijir9kdmds.png
54	22	1	2	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780631986/eidtgyycldzqsd2h1ogi.png
55	22	2	2	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780631986/eidtgyycldzqsd2h1ogi.png
56	22	3	2	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780631986/eidtgyycldzqsd2h1ogi.png
57	22	4	2	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780631986/eidtgyycldzqsd2h1ogi.png
46	22	1	1	19	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780631920/tdxpx38ylc6dvzp3plen.png
33	19	3	6	19	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780630608/sbfgkmhbhhr9ghyilsz9.png
15	18	1	1	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780630001/g8qds6wseavbfgmabzfb.png
16	18	2	1	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780630001/g8qds6wseavbfgmabzfb.png
17	18	4	1	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780630001/g8qds6wseavbfgmabzfb.png
18	18	3	1	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780630001/g8qds6wseavbfgmabzfb.png
19	18	1	4	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780630227/ejxazyw8ek8mr5ta9m9o.jpg
20	18	2	4	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780630227/ejxazyw8ek8mr5ta9m9o.jpg
21	18	3	4	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780630227/ejxazyw8ek8mr5ta9m9o.jpg
22	18	4	4	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780630227/ejxazyw8ek8mr5ta9m9o.jpg
23	18	1	3	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780630278/j8eky7prey86avgxir8o.png
24	18	2	3	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780630278/j8eky7prey86avgxir8o.png
25	18	3	3	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780630278/j8eky7prey86avgxir8o.png
26	18	4	3	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780630278/j8eky7prey86avgxir8o.png
27	19	1	1	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780630539/ax5prqx8sa6th5kdzvk2.png
28	19	2	1	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780630539/ax5prqx8sa6th5kdzvk2.png
29	19	3	1	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780630539/ax5prqx8sa6th5kdzvk2.png
30	19	4	1	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780630539/ax5prqx8sa6th5kdzvk2.png
31	19	1	6	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780630608/sbfgkmhbhhr9ghyilsz9.png
32	19	2	6	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780630608/sbfgkmhbhhr9ghyilsz9.png
34	19	4	6	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780630608/sbfgkmhbhhr9ghyilsz9.png
35	20	1	1	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780631629/vfgfaobt2ayujcdg1kr3.png
36	20	2	1	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780631629/vfgfaobt2ayujcdg1kr3.png
37	20	3	1	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780631629/vfgfaobt2ayujcdg1kr3.png
38	20	4	1	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780631629/vfgfaobt2ayujcdg1kr3.png
39	20	1	6	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780631672/prahmoalmaaez5ouperf.png
40	20	2	6	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780631672/prahmoalmaaez5ouperf.png
41	20	3	6	20	t	https://res.cloudinary.com/dzebcxlzg/image/upload/v1780631672/prahmoalmaaez5ouperf.png
\.


--
-- Data for Name: products; Type: TABLE DATA; Schema: public; Owner: neondb_owner
--

COPY public.products (id_product, name_product, price, is_active, created_at, id_category, is_featured, discount_percentage) FROM stdin;
18	Camiseta Clásica de Algodón Premium	75000.00	t	2026-06-05	1	t	10
19	Pantalón Ancho Minimalista	110000.00	t	2026-06-05	2	t	10
20	Pantalón Deportivo Casual	85000.00	t	2026-06-05	2	t	10
21	Gorro de Invierno Básico	35000.00	t	2026-06-05	3	t	10
22	Hoodie Básico con Capucha	125000.00	t	2026-06-05	1	t	10
\.


--
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: neondb_owner
--

COPY public.roles (id_role, name_role) FROM stdin;
1	ADMIN
2	CUSTOMER
\.


--
-- Data for Name: sale_details; Type: TABLE DATA; Schema: public; Owner: neondb_owner
--

COPY public.sale_details (id_sale_detail, id_sale, id_variant, quantity, unit_price, total_price) FROM stdin;
20	16	46	1	125000.00	125000.00
21	16	33	1	110000.00	110000.00
\.


--
-- Data for Name: sale_statuses; Type: TABLE DATA; Schema: public; Owner: neondb_owner
--

COPY public.sale_statuses (id_status, name_status) FROM stdin;
1	PENDING
2	PAID
3	SHIPPED
4	CANCELLED
\.


--
-- Data for Name: sales; Type: TABLE DATA; Schema: public; Owner: neondb_owner
--

COPY public.sales (id_sale, id_user, sale_date, subtotal, total, id_status, status_report, shipping_address) FROM stdin;
16	1	2026-06-05 04:04:03.920947	235000.00	235000.00	2	Pagado a través de checkout usando método: BANK	Calle mas falsaaaaa
\.


--
-- Data for Name: sizes; Type: TABLE DATA; Schema: public; Owner: neondb_owner
--

COPY public.sizes (id_size, name_size) FROM stdin;
2	S
1	XS
4	L
3	M
7	Unica
\.


--
-- Data for Name: tags; Type: TABLE DATA; Schema: public; Owner: neondb_owner
--

COPY public.tags (id_tag, name_tag) FROM stdin;
1	Y2K
2	Oversized
3	Minimalista
4	Urbano
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: neondb_owner
--

COPY public.users (id_user, first_name, last_name, email, phone_number, password_hash, id_role, is_active, created_at, address, last_access) FROM stdin;
7	Dylann	Vergara	dylann2428@gmail.com	3146009234	$2a$10$5R1TfFd//.8Au5i7AiFZ0.mDGgAnKwu0NR3ppePFzl.ubP9HWmyVG	1	t	2026-06-03 19:21:04.263569	calle 65	\N
12	Marco	Sánchez	markusiano@gmail.com	3156429565	$2a$10$TGwyJAdT4LTzY0Scxe3O8uXf/0l4uA8c0Xws/Orpj.YrJXAafNpva	2	t	2026-06-03 22:19:40.93365	Calle 74	\N
6	Santy	Sanchez	Santy@example.com	3111234567	$2a$10$ly8Sz.oBYGDtuYkfssv1eeWsYvc26iaK.VwtBTlhGXZ3hG3XbZkBW	2	t	2026-06-03 10:11:46.256567	Pepe	2026-06-05 01:27:28.999575
1	Pedro	Perez	pepepe@example.com	3111234568	$2a$10$ufs1nQ.hHasxmw7CqlhHweTUIYEUst0e7SXngzd0WR89XNfstbWMK	1	t	2026-05-27 19:57:47.097329	Calle mas falsaaaaa	2026-06-05 01:31:24.204594
\.


--
-- Name: cart_items_item_cart_id_seq; Type: SEQUENCE SET; Schema: public; Owner: neondb_owner
--

SELECT pg_catalog.setval('public.cart_items_item_cart_id_seq', 39, true);


--
-- Name: carts_cart_id_seq; Type: SEQUENCE SET; Schema: public; Owner: neondb_owner
--

SELECT pg_catalog.setval('public.carts_cart_id_seq', 17, true);


--
-- Name: categories_id_category_seq; Type: SEQUENCE SET; Schema: public; Owner: neondb_owner
--

SELECT pg_catalog.setval('public.categories_id_category_seq', 5, true);


--
-- Name: colors_id_color_seq; Type: SEQUENCE SET; Schema: public; Owner: neondb_owner
--

SELECT pg_catalog.setval('public.colors_id_color_seq', 6, true);


--
-- Name: payment_statuses_id_status_seq; Type: SEQUENCE SET; Schema: public; Owner: neondb_owner
--

SELECT pg_catalog.setval('public.payment_statuses_id_status_seq', 4, true);


--
-- Name: payments_id_payment_seq; Type: SEQUENCE SET; Schema: public; Owner: neondb_owner
--

SELECT pg_catalog.setval('public.payments_id_payment_seq', 5, true);


--
-- Name: product_variants_id_variant_seq; Type: SEQUENCE SET; Schema: public; Owner: neondb_owner
--

SELECT pg_catalog.setval('public.product_variants_id_variant_seq', 57, true);


--
-- Name: products_id_product_seq; Type: SEQUENCE SET; Schema: public; Owner: neondb_owner
--

SELECT pg_catalog.setval('public.products_id_product_seq', 22, true);


--
-- Name: roles_id_role_seq; Type: SEQUENCE SET; Schema: public; Owner: neondb_owner
--

SELECT pg_catalog.setval('public.roles_id_role_seq', 2, true);


--
-- Name: sale_details_id_sale_detail_seq; Type: SEQUENCE SET; Schema: public; Owner: neondb_owner
--

SELECT pg_catalog.setval('public.sale_details_id_sale_detail_seq', 21, true);


--
-- Name: sale_statuses_id_status_seq; Type: SEQUENCE SET; Schema: public; Owner: neondb_owner
--

SELECT pg_catalog.setval('public.sale_statuses_id_status_seq', 4, true);


--
-- Name: sales_id_sale_seq; Type: SEQUENCE SET; Schema: public; Owner: neondb_owner
--

SELECT pg_catalog.setval('public.sales_id_sale_seq', 16, true);


--
-- Name: sizes_id_size_seq; Type: SEQUENCE SET; Schema: public; Owner: neondb_owner
--

SELECT pg_catalog.setval('public.sizes_id_size_seq', 7, true);


--
-- Name: tags_id_tag_seq; Type: SEQUENCE SET; Schema: public; Owner: neondb_owner
--

SELECT pg_catalog.setval('public.tags_id_tag_seq', 6, true);


--
-- Name: users_id_user_seq; Type: SEQUENCE SET; Schema: public; Owner: neondb_owner
--

SELECT pg_catalog.setval('public.users_id_user_seq', 15, true);


--
-- Name: cart_items cart_items_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.cart_items
    ADD CONSTRAINT cart_items_pkey PRIMARY KEY (item_cart_id);


--
-- Name: carts carts_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.carts
    ADD CONSTRAINT carts_pkey PRIMARY KEY (cart_id);


--
-- Name: categories categories_name_category_key; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_name_category_key UNIQUE (name_category);


--
-- Name: categories categories_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (id_category);


--
-- Name: colors colors_name_color_key; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.colors
    ADD CONSTRAINT colors_name_color_key UNIQUE (name_color);


--
-- Name: colors colors_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.colors
    ADD CONSTRAINT colors_pkey PRIMARY KEY (id_color);


--
-- Name: payment_statuses payment_statuses_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.payment_statuses
    ADD CONSTRAINT payment_statuses_pkey PRIMARY KEY (id_status);


--
-- Name: payment_statuses payment_statuses_status_name_key; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.payment_statuses
    ADD CONSTRAINT payment_statuses_status_name_key UNIQUE (status_name);


--
-- Name: payments payments_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.payments
    ADD CONSTRAINT payments_pkey PRIMARY KEY (id_payment);


--
-- Name: product_tags product_tags_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.product_tags
    ADD CONSTRAINT product_tags_pkey PRIMARY KEY (id_product, id_tag);


--
-- Name: product_variants product_variants_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.product_variants
    ADD CONSTRAINT product_variants_pkey PRIMARY KEY (id_variant);


--
-- Name: products products_name_product_key; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_name_product_key UNIQUE (name_product);


--
-- Name: products products_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT products_pkey PRIMARY KEY (id_product);


--
-- Name: roles roles_name_role_key; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_name_role_key UNIQUE (name_role);


--
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id_role);


--
-- Name: sale_details sale_details_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.sale_details
    ADD CONSTRAINT sale_details_pkey PRIMARY KEY (id_sale_detail);


--
-- Name: sale_statuses sale_statuses_name_status_key; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.sale_statuses
    ADD CONSTRAINT sale_statuses_name_status_key UNIQUE (name_status);


--
-- Name: sale_statuses sale_statuses_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.sale_statuses
    ADD CONSTRAINT sale_statuses_pkey PRIMARY KEY (id_status);


--
-- Name: sales sales_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.sales
    ADD CONSTRAINT sales_pkey PRIMARY KEY (id_sale);


--
-- Name: sizes sizes_name_size_key; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.sizes
    ADD CONSTRAINT sizes_name_size_key UNIQUE (name_size);


--
-- Name: sizes sizes_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.sizes
    ADD CONSTRAINT sizes_pkey PRIMARY KEY (id_size);


--
-- Name: tags tags_name_tag_key; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.tags
    ADD CONSTRAINT tags_name_tag_key UNIQUE (name_tag);


--
-- Name: tags tags_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.tags
    ADD CONSTRAINT tags_pkey PRIMARY KEY (id_tag);


--
-- Name: cart_items unique_cart_variant; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.cart_items
    ADD CONSTRAINT unique_cart_variant UNIQUE (cart_id, product_variant_id);


--
-- Name: product_variants unique_product_variant; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.product_variants
    ADD CONSTRAINT unique_product_variant UNIQUE (id_product, id_size, id_color);


--
-- Name: sale_details unique_sale_variant; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.sale_details
    ADD CONSTRAINT unique_sale_variant UNIQUE (id_sale, id_variant);


--
-- Name: carts unique_user_cart; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.carts
    ADD CONSTRAINT unique_user_cart UNIQUE (user_id);


--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id_user);


--
-- Name: cart_items fk_cart; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.cart_items
    ADD CONSTRAINT fk_cart FOREIGN KEY (cart_id) REFERENCES public.carts(cart_id) ON DELETE CASCADE;


--
-- Name: sale_details fk_detail_sale; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.sale_details
    ADD CONSTRAINT fk_detail_sale FOREIGN KEY (id_sale) REFERENCES public.sales(id_sale) ON DELETE CASCADE;


--
-- Name: sale_details fk_detail_variant; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.sale_details
    ADD CONSTRAINT fk_detail_variant FOREIGN KEY (id_variant) REFERENCES public.product_variants(id_variant);


--
-- Name: payments fk_payment_sale; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.payments
    ADD CONSTRAINT fk_payment_sale FOREIGN KEY (id_sale) REFERENCES public.sales(id_sale) ON DELETE CASCADE;


--
-- Name: payments fk_payment_status; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.payments
    ADD CONSTRAINT fk_payment_status FOREIGN KEY (id_status) REFERENCES public.payment_statuses(id_status);


--
-- Name: products fk_product_category; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT fk_product_category FOREIGN KEY (id_category) REFERENCES public.categories(id_category);


--
-- Name: product_tags fk_product_tag; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.product_tags
    ADD CONSTRAINT fk_product_tag FOREIGN KEY (id_tag) REFERENCES public.tags(id_tag) ON DELETE CASCADE;


--
-- Name: cart_items fk_product_variant; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.cart_items
    ADD CONSTRAINT fk_product_variant FOREIGN KEY (product_variant_id) REFERENCES public.product_variants(id_variant) ON DELETE CASCADE;


--
-- Name: sales fk_sale_status; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.sales
    ADD CONSTRAINT fk_sale_status FOREIGN KEY (id_status) REFERENCES public.sale_statuses(id_status);


--
-- Name: sales fk_sale_user; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.sales
    ADD CONSTRAINT fk_sale_user FOREIGN KEY (id_user) REFERENCES public.users(id_user);


--
-- Name: product_tags fk_tag_product; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.product_tags
    ADD CONSTRAINT fk_tag_product FOREIGN KEY (id_product) REFERENCES public.products(id_product) ON DELETE CASCADE;


--
-- Name: users fk_user_role; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fk_user_role FOREIGN KEY (id_role) REFERENCES public.roles(id_role);


--
-- Name: product_variants fk_variant_color; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.product_variants
    ADD CONSTRAINT fk_variant_color FOREIGN KEY (id_color) REFERENCES public.colors(id_color);


--
-- Name: product_variants fk_variant_product; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.product_variants
    ADD CONSTRAINT fk_variant_product FOREIGN KEY (id_product) REFERENCES public.products(id_product);


--
-- Name: product_variants fk_variant_size; Type: FK CONSTRAINT; Schema: public; Owner: neondb_owner
--

ALTER TABLE ONLY public.product_variants
    ADD CONSTRAINT fk_variant_size FOREIGN KEY (id_size) REFERENCES public.sizes(id_size);


--
-- Name: DEFAULT PRIVILEGES FOR SEQUENCES; Type: DEFAULT ACL; Schema: public; Owner: cloud_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE cloud_admin IN SCHEMA public GRANT ALL ON SEQUENCES TO neon_superuser WITH GRANT OPTION;


--
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: public; Owner: cloud_admin
--

ALTER DEFAULT PRIVILEGES FOR ROLE cloud_admin IN SCHEMA public GRANT ALL ON TABLES TO neon_superuser WITH GRANT OPTION;


--
-- PostgreSQL database dump complete
--

\unrestrict sZ8w2sZvHK4eTf3bfKMkoiRuJG7llUQECNX6R2Z4PIilHmEaeOZWAyLnXtwqabT

