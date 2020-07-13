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

#include "bin_t.h"
#include "bin_config.h"

/*COAP SERVER*/
extern coap_resource_t res_leds;
extern coap_resource_t res_action;

/*GLOBAL STRUCTURES*/
bin_t bin;

/*UTILITY FUNCTIONS*/
void client_chunk_handler(coap_message_t *response)
{
  const uint8_t *chunk;

  if(response == NULL) {
    puts("Request timed out");
    return;
  }

  int len = coap_get_payload(response, &chunk);

  printf("|%.*s", len, (char *)chunk);
}

/*---------------------------------------------------------------------------*/
//PROCESS(hello_world_process, "Hello world process");
PROCESS(startup_process, "Startup process");
PROCESS(server_process, "server process");
//server status process
//server locked
//button process
//btn process

AUTOSTART_PROCESSES(&startup_process);

static struct etimer et;
/*---------------------------------------------------------------------------*/
/*PROCESS_THREAD(hello_world_process, ev, data)
{
  static struct etimer timer;

  PROCESS_BEGIN();

 
  etimer_set(&timer, CLOCK_SECOND * 10);

  while(1) {
    printf("Hello, world\n");

   
    PROCESS_WAIT_EVENT_UNTIL(etimer_expired(&timer));
    etimer_reset(&timer);
  }

  PROCESS_END();
}*/
/*---------------------------------------------------------------------------*/
PROCESS_THREAD(startup_process, ev, data)
{
  static coap_endpoint_t server_ep;

  PROCESS_BEGIN();

  static coap_message_t request[1];

  static char msg[40];

// ---------------------------- BIN INITIALIZATION ----------------------------
  
  bin.id = node_id;
  bin.capacity = standard_capacity[rand() % 4];
  bin.type = rand() % 4;
  bin.locked = 0;
  bin.status = 0;

  printf("Initializing Bin!\nbin_id:%d\nbin_capacity:%d\nbin_type:%d\n",bin.id,bin.capacity,bin.type);

// ---------------------------- BIN REGISTRATION ----------------------------

  /* Register Bin to the cloud application */

  coap_endpoint_parse(SERVER_EP, strlen(SERVER_EP), &server_ep);

  etimer_set(&et, TOGGLE_INTERVAL * CLOCK_SECOND);

  while (1)
  {
      PROCESS_YIELD();

      if(etimer_expired(&et)) {

        coap_init_message(request, COAP_TYPE_CON, COAP_POST, 0);
        coap_set_header_uri_path(request, service_urls[0]);

        //const char msg[] = "Bin1!";
        sprintf(msg,"{\"id\":%d,\"type\":%d,\"capacity\":%d}", bin.id, bin.type, bin.capacity);

        coap_set_payload(request, (uint8_t *)msg, sizeof(msg) - 1);
        
        printf(">> Sending registration request!\n");

        COAP_BLOCKING_REQUEST(&server_ep, request, client_chunk_handler);

        printf(">> Registered!\n");

        break;

        //etimer_reset(&et);
      }

  }

  process_start(&server_process, NULL);

  PROCESS_END();
}

PROCESS_THREAD(server_process, ev, data)
{
  PROCESS_BEGIN();

  PROCESS_PAUSE();

  printf("Starting Erbium Example Server\n");

  coap_activate_resource(&res_leds, "locked");
  coap_activate_resource(&res_action, "action");

  while(1) {
    PROCESS_WAIT_EVENT();

  }
  PROCESS_END();
}
