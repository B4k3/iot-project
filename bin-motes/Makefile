CONTIKI_PROJECT = smart-bin
all: $(CONTIKI_PROJECT)

MODULES_REL += ./resources
CONTIKI = ../..

include $(CONTIKI)/Makefile.dir-variables
MODULES += $(CONTIKI_NG_APP_LAYER_DIR)/coap
CFLAGS += -DPROJECT_CONF_H=\"project-conf.h\"

include $(CONTIKI)/Makefile.include