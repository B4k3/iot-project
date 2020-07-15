/*
 * Copyright (c) 2006, Swedish Institute of Computer Science.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * This file is part of the Contiki operating system.
 *
 */

/**
 * \file
 *         A very simple Contiki application showing how Contiki programs look
 * \author
 *         Adam Dunkels <adam@sics.se>
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "contiki.h"
#include "contiki-net.h"
#include "coap-engine.h"
#include "coap-blocking-api.h"
#include "node-id.h"
#include "dev/button-hal.h"
#include "dev/button-sensor.h"
#include "dev/leds.h"

#include "bin_t.h"
#include "bin_config.h"

/*COAP SERVER*/
extern coap_resource_t res_lock;
extern coap_resource_t res_unlock;
extern coap_resource_t res_empty;
extern coap_resource_t res_status;
/*GLOBAL STRUCTURES*/
bin_t bin;
int init;

/*UTILITY FUNCTIONS*/
void client_chunk_handler(coap_message_t *response)
{
  const uint8_t *chunk;

  if(response == NULL) {
    puts(">> Request timed out\n");
    init = 0;
    return;
  }
  else {
    init =1;
    return;
  }


  int len = coap_get_payload(response, &chunk);

  printf("|%.*s", len, (char *)chunk);
}

/*---------------------------------------------------------------------------*/
//PROCESS(hello_world_process, "Hello world process");
PROCESS(startup_process, "Startup process");
PROCESS(server_process, "server process");
PROCESS(btn_process, "button process");
//button process

AUTOSTART_PROCESSES(&startup_process);

static struct etimer et;
/*---------------------------------------------------------------------------*/
PROCESS_THREAD(startup_process, ev, data)
{
  static coap_endpoint_t server_ep;

  PROCESS_BEGIN();

  static coap_message_t request[1];

  static char msg[50];

// ---------------------------- BIN INITIALIZATION ----------------------------
  
  bin.id = node_id;
  bin.capacity = standard_capacity[rand() % 4];
  bin.type = rand() % 4;
  bin.locked = 0;
  bin.status = 0;

  init = 0;

  printf(">> Initializing Bin!\n[bin_id: %d]\n[bin_capacity: %d]\n[bin_type: (%d)%s]\n",bin.id,bin.capacity,bin.type,type[bin.type]);

// ---------------------------- BIN REGISTRATION ----------------------------

  /* Register Bin to the cloud application */

  coap_endpoint_parse(SERVER_EP, strlen(SERVER_EP), &server_ep);

  process_start(&server_process, NULL);

  etimer_set(&et, TOGGLE_INTERVAL * CLOCK_SECOND);

  while (1)
  {
      PROCESS_YIELD();

      if(etimer_expired(&et)) {

        coap_init_message(request, COAP_TYPE_CON, COAP_POST, 0);
        coap_set_header_uri_path(request, service_urls[0]);
        coap_set_header_content_format(request, APPLICATION_JSON);

        snprintf(msg, COAP_MAX_CHUNK_SIZE, "{\"id\":%d,\"type\":%d,\"capacity\":%d}", bin.id, bin.type, bin.capacity);
        
        coap_set_payload(request, (uint8_t *)msg, strlen(msg));

        printf(">> Sending registration request!\n");

        COAP_BLOCKING_REQUEST(&server_ep, request, client_chunk_handler);

        if(init == 1) {
          break;
        }
        else
        {
          printf(">> A new attempt will be performed in 10s!\n");
          etimer_reset(&et);
        }
      }

  }

  process_start(&btn_process, NULL);

  PROCESS_END();
}

PROCESS_THREAD(server_process, ev, data)
{
  PROCESS_BEGIN();

  PROCESS_PAUSE();

  printf(">> Starting Erbium Server\n");

  coap_activate_resource(&res_lock, "lock");
  coap_activate_resource(&res_unlock, "unlock");
  coap_activate_resource(&res_empty, "action");
  coap_activate_resource(&res_status, "status");

  while(1) {
    PROCESS_WAIT_EVENT();

  }
  PROCESS_END();
}

PROCESS_THREAD(btn_process, ev, data)
{
  button_hal_button_t *btn;
  int input;
  PROCESS_BEGIN();
  printf(">> Bin Active!\n");

  btn = button_hal_get_by_index(0); 

  while(1) { 
    PROCESS_YIELD();
    if(ev == button_hal_press_event) {
      if(bin.locked != 1){
        input = rand() % MAXIMUM_IN;
        bin.status += input;
        printf(">> Status update:\n + %d Kg\n Bin status: %d\n",input, bin.status);
        if(bin.status >= bin.capacity) {
          bin.locked = 1;
          uint8_t led = LEDS_RED;
          leds_on(led);
          printf(">> Maximum capacity reachead!\n Bin locked!\n");
        }
        res_status.trigger();
      }
      else {
          printf(">> Bin locked! \n");
      }
    }
  }

  PROCESS_END();
}
